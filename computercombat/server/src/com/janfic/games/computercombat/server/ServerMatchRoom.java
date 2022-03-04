package com.janfic.games.computercombat.server;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.network.server.MatchClient;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.match.Match;
import com.janfic.games.computercombat.model.match.MatchData;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.List;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom {

    // TODO: add final
    private Player player1, player2;
    private Match match;
    private MatchData matchData;
    private boolean isGameOver;
    private Thread thread;

    public ServerMatchRoom(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Begin the match
                match = new Match(player1, player2);
                player1.beginMatch(match.getPlayerMatchState(player1.getUID()), player2);
                player2.beginMatch(match.getPlayerMatchState(player2.getUID()), player1);

                // set up match data collection
                Timestamp starttime = new Timestamp(System.currentTimeMillis());
                matchData = new MatchData(player1, player2);
                matchData.getMatchStates().add(new MatchState(match.getCurrentState()));

                // Start main match loop
                isGameOver = false;
                while (isGameOver == false) {
                    // get move of current player
                    String whosMoveUID = match.whosMove();
                    Player currentPlayer = whosMoveUID.equals(player1.getUID()) ? player1 : player2;
                    Player otherPlayer = !whosMoveUID.equals(player1.getUID()) ? player1 : player2;

                    Move move = currentPlayer.getMove();

                    if (move == null) {
                        isGameOver = true;
                        break;
                    }

                    // apply the move
                    if (move instanceof UseAbilityMove) {
                        UseAbilityMove m = (UseAbilityMove) move;
                        m.getCard().setAbility(Ability.getAbilityFromCode(m.getCard().getAbility()));
                    }
                    boolean isValid = match.isValidMove(move);
                    if (isValid) {
                        List<MoveResult> results = match.makeMove(move);
                        currentPlayer.updateState(results);
                        otherPlayer.updateState(results);
                        matchData.add(move, results, match.getCurrentState());
                    }

                    isGameOver = match.getCurrentState().isGameOver;
                }

                //Build MatchData
                Timestamp endtime = new Timestamp(System.currentTimeMillis());
                MatchState lastState = matchData.getMatchStates().get(matchData.getMatchStates().size() - 1);
                if (lastState.winner != null) {
                    matchData.setWinner(lastState.winner.getUID().equals(player2.getUID()));
                } else {
                    matchData.setWinner(false);
                }

                matchData.setStartTime(starttime);
                matchData.setEndTime(endtime);

                // Calculate Rewards
                HashMap<String, String> rewards1 = new HashMap<>();
                HashMap<String, String> rewards2 = new HashMap<>();
                calculateRewards(rewards1, rewards2);

                MatchResults results1 = new MatchResults(matchData.getRewards().get(player1.getUID()), starttime, endtime, player2, !matchData.getWinner(), rewards1);
                MatchResults results2 = new MatchResults(matchData.getRewards().get(player2.getUID()), starttime, endtime, player1, matchData.getWinner(), rewards2);

                player1.gameOver(results1);
                player2.gameOver(results2);

                // Insert Match Data to DB
                // Update Profiles ( packets )
                System.out.println("END OF MATCH");
            }
        });
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void start() {
        thread.start();
    }

    public void calculateRewards(HashMap<String, String> rewards1, HashMap<String, String> rewards2) {
        rewards1.put("Collected", "" + matchData.getRewards().get(player1.getUID()));
        rewards2.put("Collected", "" + matchData.getRewards().get(player2.getUID()));
        if (matchData.getWinner()) {
            matchData.getRewards().put(player2.getUID(), matchData.getRewards().get(player2.getUID()) + 25);
            rewards2.put("Victory Bonus", "" + 25);
        } else {
            matchData.getRewards().put(player1.getUID(), matchData.getRewards().get(player1.getUID()) + 25);
            rewards1.put("Victory Bonus", "" + 25);
        }
    }

    public MatchData getMatchData() {
        return matchData;
    }

    /*
    public ServerMatchRoom(MatchClient matchClient1, MatchClient matchClient2) {
        this.matchClient1 = matchClient1;
        this.matchClient2 = matchClient2;
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Move to Server 
                    // #####
                    Message message1 = new Message(Type.FOUND_MATCH, ServerMatchRoom.this.matchClient2.getProfile().getName());
                    Message message2 = new Message(Type.FOUND_MATCH, ServerMatchRoom.this.matchClient1.getProfile().getName());

                    matchClient1.sendMessage(message1);
                    matchClient2.sendMessage(message2);

                    while (matchClient1.hasMessage() == false || matchClient2.hasMessage() == false) {
                    }

                    Message response1 = matchClient1.readMessage();
                    Message response2 = matchClient2.readMessage();

                    if (response1.type == Type.CANCEL_QUEUE) {
                        Message error = new Message(Type.ERROR, "A PLAYER WASNT READY");
                        matchClient1.sendMessage(new Message(Type.SUCCESS, "LEFT QUEUE"));
                        matchClient2.sendMessage(error);
                    }

                    if (response2.type == Type.CANCEL_QUEUE) {
                        Message error = new Message(Type.ERROR, "A PLAYER WASNT READY");
                        matchClient2.sendMessage(new Message(Type.SUCCESS, "LEFT QUEUE"));
                        matchClient1.sendMessage(error);
                    }
                    //#####

                    Json json = new Json();
                    json.setSerializer(ObjectMap.class, new ObjectMapSerializer());

                    // Begin Match
                    Match match = new Match(matchClient1.getProfile(), matchClient2.getProfile(), matchClient1.getDeck(), matchClient2.getDeck());
                    Message matchData1 = new Message(Type.MATCH_STATE_DATA, json.toJson(match.getPlayerMatchState(matchClient1.getProfile().getUID())));
                    Message matchData2 = new Message(Type.MATCH_STATE_DATA, json.toJson(match.getPlayerMatchState(matchClient2.getProfile().getUID())));
                    Timestamp starttime = new Timestamp(System.currentTimeMillis());

                    matchClient1.sendMessage(matchData1);
                    matchClient2.sendMessage(matchData2);

                    // End Match Data
                    matchData = new MatchData(matchClient1.getProfile(), matchClient2.getProfile(), matchClient1.getDeck(), matchClient2.getDeck());
                    matchData.getMatchStates().add(match.getCurrentState());

                    // Main Match Loop
                    while (isGameOver == false) {
                        // Differentiate players ( whos move to wait for? )
                        String currentPlayersMove = match.whosMove();
                        MatchClient currentMatchClient = matchClient1.getProfile().getUID().equals(currentPlayersMove) ? matchClient1 : matchClient2;
                        MatchClient otherMatchClient = matchClient1.getProfile().getUID().equals(currentPlayersMove) ? matchClient2 : matchClient1;

                        // Start Waiting for message
                        float timeStart = System.nanoTime();
                        float delta = 0;
                        // Loop Breaks if message or timeout
                        while (currentMatchClient.hasMessage() == false && currentMatchClient.getSocket().isConnected()) {
                            delta = System.nanoTime() - timeStart;
                            if (delta / 1000000000f >= 10) {
                                try {
                                    currentMatchClient.sendMessage(new Message(Type.PING, "PING"));
                                    otherMatchClient.sendMessage(new Message(Type.PING, "PING"));
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
                        Message moveMessage = currentMatchClient.readMessage();
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
                                currentMatchClient.sendMessage(response);
                                otherMatchClient.sendMessage(response);
                                isGameOver = match.getCurrentState().isGameOver;
                            } else {
                                // Send Rejected message
                                Message notValidMessage = new Message(Type.MOVE_REJECT, "NOT VALID MOVE");
                                currentMatchClient.sendMessage(notValidMessage);
                            }
                        }
                    } // Match Loop

                    // Calculate Match Results
                    MatchState lastState = matchData.getMatchStates().get(matchData.getMatchStates().size() - 1);
                    if (lastState.winner != null) {
                        matchData.setWinner(lastState.winner.getUID().equals(matchClient2.getProfile().getUID()));
                    } else {
                        matchData.setWinner(false);
                    }

                    Timestamp endtime = new Timestamp(System.currentTimeMillis());
                    matchData.setStartTime(starttime);
                    matchData.setEndTime(endtime);

                    // Calculate Rewards
                    HashMap<String, String> rewards1 = new HashMap<>();
                    HashMap<String, String> rewards2 = new HashMap<>();
                    rewards1.put("Collected", "" + matchData.getRewards().get(matchClient1.getProfile().getUID()));
                    rewards2.put("Collected", "" + matchData.getRewards().get(matchClient2.getProfile().getUID()));
                    if (matchData.getWinner()) {
                        matchData.getRewards().put(matchClient2.getProfile().getUID(), matchData.getRewards().get(matchClient2.getProfile().getUID()) + 25);
                        rewards2.put("Victory Bonus", "" + 25);
                    } else {
                        matchData.getRewards().put(matchClient1.getProfile().getUID(), matchData.getRewards().get(matchClient1.getProfile().getUID()) + 25);
                        rewards1.put("Victory Bonus", "" + 25);
                    }

                    // Delay next message
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    // Send Match Results to Clients
                    MatchResults results1 = new MatchResults(matchData.getRewards().get(matchClient1.getProfile().getUID()), starttime, endtime, matchClient2.getProfile(), !matchData.getWinner(), rewards1);
                    MatchResults results2 = new MatchResults(matchData.getRewards().get(matchClient2.getProfile().getUID()), starttime, endtime, matchClient1.getProfile(), matchData.getWinner(), rewards2);

                    Message results1Message = new Message(Type.MATCH_RESULTS, json.toJson(results1));
                    Message results2Message = new Message(Type.MATCH_RESULTS, json.toJson(results2));
                    matchClient1.sendMessage(results1Message);
                    matchClient2.sendMessage(results2Message);

                    matchClient1.getProfile().setPackets(matchClient1.getProfile().getPackets() + matchData.getRewards().get(matchClient1.getProfile().getUID()));
                    matchClient2.getProfile().setPackets(matchClient2.getProfile().getPackets() + matchData.getRewards().get(matchClient2.getProfile().getUID()));

                    // Insert Match Data to DB
                    int updates = SQLAPI.getSingleton().recordMatchData(matchData);
                    SQLAPI.getSingleton().saveProfile(matchClient1.getProfile());
                    SQLAPI.getSingleton().saveProfile(matchClient2.getProfile());
                } catch (IOException e) {
                }
            }
        });

    }
     */
}
