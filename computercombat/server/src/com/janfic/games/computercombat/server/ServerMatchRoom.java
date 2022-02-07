package com.janfic.games.computercombat.server;

import com.janfic.games.computercombat.network.server.MatchClient;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.match.Match;
import com.janfic.games.computercombat.model.match.MatchData;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.util.ObjectMapSerializer;
import java.io.IOException;
import java.util.List;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom {

    /*
    Plan A:
        Differentiate HumanPlayer vs all others to wait for client message
    Plan B:
        Implement HumanPlayer to wait for client message seperately
    
     */
    private final MatchClient player1, player2;
    private Match match;
    private MatchData matchData;
    private boolean isGameOver;
    private Thread thread;

    public ServerMatchRoom(MatchClient player1, MatchClient player2) {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message1 = new Message(Type.FOUND_MATCH, ServerMatchRoom.this.player2.getProfile().getName());
                    Message message2 = new Message(Type.FOUND_MATCH, ServerMatchRoom.this.player1.getProfile().getName());

                    player1.sendMessage(message1);
                    player2.sendMessage(message2);

                    while (player1.hasMessage() == false || player2.hasMessage() == false) {
                    }

                    Message response1 = player1.readMessage();
                    Message response2 = player2.readMessage();

                    if (response1.type == Type.CANCEL_QUEUE) {
                        Message error = new Message(Type.ERROR, "A PLAYER WASNT READY");
                        player1.sendMessage(new Message(Type.SUCCESS, "LEFT QUEUE"));
                        player2.sendMessage(error);
                    }

                    if (response2.type == Type.CANCEL_QUEUE) {
                        Message error = new Message(Type.ERROR, "A PLAYER WASNT READY");
                        player2.sendMessage(new Message(Type.SUCCESS, "LEFT QUEUE"));
                        player1.sendMessage(error);
                    }

                    Json json = new Json();
                    json.setSerializer(ObjectMap.class, new ObjectMapSerializer());

                    // Begin Match
                    Match match = new Match(player1.getProfile(), player2.getProfile(), player1.getDeck(), player2.getDeck());
                    Message matchData1 = new Message(Type.MATCH_STATE_DATA, json.toJson(match.getPlayerMatchState(player1.getProfile().getUID())));
                    Message matchData2 = new Message(Type.MATCH_STATE_DATA, json.toJson(match.getPlayerMatchState(player2.getProfile().getUID())));
                    Timestamp starttime = new Timestamp(System.currentTimeMillis());

                    player1.sendMessage(matchData1);
                    player2.sendMessage(matchData2);

                    // Match Data = End Match Data
                    matchData = new MatchData(player1.getProfile(), player2.getProfile(), player1.getDeck(), player2.getDeck());
                    matchData.getMatchStates().add(match.getCurrentState());

                    // Main Match Loop
                    while (isGameOver == false) {
                        // Differentiate players ( whos move to wait for? )
                        String currentPlayersMove = match.whosMove();
                        MatchClient currentPlayer = player1.getProfile().getUID().equals(currentPlayersMove) ? player1 : player2;
                        MatchClient otherPlayer = player1.getProfile().getUID().equals(currentPlayersMove) ? player2 : player1;

                        // Start Waiting for message
                        float timeStart = System.nanoTime();
                        float delta = 0;
                        // Loop Breaks if message or timeout
                        while (currentPlayer.hasMessage() == false && currentPlayer.getSocket().isConnected()) {
                            delta = System.nanoTime() - timeStart;
                            if (delta / 1000000000f >= 10) {
                                try {
                                    currentPlayer.sendMessage(new Message(Type.PING, "PING"));
                                    otherPlayer.sendMessage(new Message(Type.PING, "PING"));
                                    timeStart = System.nanoTime();
                                } catch (Exception e) {
                                    isGameOver = true;
                                    break;
                                }
                            }
                        }

                        // End Game if Game Over
                        if (isGameOver) {
                            break;
                        }

                        // Recieved Message
                        // Read and Calculate
                        Message moveMessage = currentPlayer.readMessage();
                        if (moveMessage.type == Type.MOVE_REQUEST) {

                            // Deserialize
                            String content = moveMessage.getMessage();
                            Move move;
                            move = json.fromJson(Move.class, content);

                            // If Ability move, compile ability code
                            if (move instanceof UseAbilityMove) {
                                UseAbilityMove m = (UseAbilityMove) move;
                                m.getCard().setAbility(Ability.getAbilityFromCode(m.getCard().getAbility()));
                            }

                            // If valid move, calculate results and send to players
                            boolean isValid = match.isValidMove(move);
                            if (isValid) {
                                // Calculate results
                                List<MoveResult> results = match.makeMove(move);
                                matchData.add(move, results, match.getCurrentState());
                                // Serialize and Send
                                Message response = new Message(Type.MOVE_ACCEPT, json.toJson(results));
                                currentPlayer.sendMessage(response);
                                otherPlayer.sendMessage(response);
                                isGameOver = match.getCurrentState().isGameOver;
                            } else {
                                // Send Rejected message
                                Message notValidMessage = new Message(Type.MOVE_REJECT, "NOT VALID MOVE");
                                currentPlayer.sendMessage(notValidMessage);
                            }
                        }
                    } // Match Loop

                    // Calculate Match Results
                    MatchState lastState = matchData.getMatchStates().get(matchData.getMatchStates().size() - 1);
                    if (lastState.winner != null) {
                        matchData.setWinner(lastState.winner.getUID().equals(player2.getProfile().getUID()));
                    } else {
                        matchData.setWinner(false);
                    }

                    Timestamp endtime = new Timestamp(System.currentTimeMillis());
                    matchData.setStartTime(starttime);
                    matchData.setEndTime(endtime);

                    // Calculate Rewards
                    HashMap<String, String> rewards1 = new HashMap<>();
                    HashMap<String, String> rewards2 = new HashMap<>();
                    rewards1.put("Collected", "" + matchData.getRewards().get(player1.getProfile().getUID()));
                    rewards2.put("Collected", "" + matchData.getRewards().get(player2.getProfile().getUID()));
                    if (matchData.getWinner()) {
                        matchData.getRewards().put(player2.getProfile().getUID(), matchData.getRewards().get(player2.getProfile().getUID()) + 25);
                        rewards2.put("Victory Bonus", "" + 25);
                    } else {
                        matchData.getRewards().put(player1.getProfile().getUID(), matchData.getRewards().get(player1.getProfile().getUID()) + 25);
                        rewards1.put("Victory Bonus", "" + 25);
                    }

                    // Delay next message
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    // Send Match Results to Clients
                    MatchResults results1 = new MatchResults(matchData.getRewards().get(player1.getProfile().getUID()), starttime, endtime, player2.getProfile(), !matchData.getWinner(), rewards1);
                    MatchResults results2 = new MatchResults(matchData.getRewards().get(player2.getProfile().getUID()), starttime, endtime, player1.getProfile(), matchData.getWinner(), rewards2);

                    Message results1Message = new Message(Type.MATCH_RESULTS, json.toJson(results1));
                    Message results2Message = new Message(Type.MATCH_RESULTS, json.toJson(results2));
                    player1.sendMessage(results1Message);
                    player2.sendMessage(results2Message);

                    player1.getProfile().setPackets(player1.getProfile().getPackets() + matchData.getRewards().get(player1.getProfile().getUID()));
                    player2.getProfile().setPackets(player2.getProfile().getPackets() + matchData.getRewards().get(player2.getProfile().getUID()));

                    // Insert Match Data to DB
                    int updates = SQLAPI.getSingleton().recordMatchData(matchData);
                    SQLAPI.getSingleton().saveProfile(player1.getProfile());
                    SQLAPI.getSingleton().saveProfile(player2.getProfile());
                } catch (IOException e) {
                }
            }
        });
        this.player1 = player1;
        this.player2 = player2;
    }

    public MatchClient getPlayer1() {
        return player1;
    }

    public MatchClient getPlayer2() {
        return player2;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void start() {
        thread.start();
    }
}
