package com.janfic.games.computercombat.server;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.Match;
import com.janfic.games.computercombat.model.moves.MatchComponentsMove;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom {

    private final MatchClient player1, player2;
    private Match match;
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
                    Match match = new Match(player1.getProfile(), player2.getProfile(), player1.getDeck(), player2.getDeck());
                    Message matchData1 = new Message(Type.MATCH_STATE_DATA, json.prettyPrint(match.getPlayerMatchState(player1.getProfile().getUID())));
                    Message matchData2 = new Message(Type.MATCH_STATE_DATA, json.prettyPrint(match.getPlayerMatchState(player2.getProfile().getUID())));

                    player1.sendMessage(matchData1);
                    player2.sendMessage(matchData2);

                    while (isGameOver == false) {
                        String currentPlayersMove = match.whosMove();
                        MatchClient currentPlayer = player1.getProfile().getUID().equals(currentPlayersMove) ? player1 : player2;
                        MatchClient otherPlayer = player1.getProfile().getUID().equals(currentPlayersMove) ? player2 : player1;

                        float timeStart = System.nanoTime();
                        float delta = 0;
                        boolean disconnected = false;
                        while (currentPlayer.hasMessage() == false && currentPlayer.getSocket().isConnected()) {
                            delta = System.nanoTime() - timeStart;
                            if (delta / 1000000000f >= 10) {
                                try {
                                    currentPlayer.sendMessage(new Message(Type.PING, "PING"));
                                    otherPlayer.sendMessage(new Message(Type.PING, "PING"));
                                    timeStart = System.nanoTime();
                                } catch (Exception e) {
                                    disconnected = true;
                                    isGameOver = true;
                                    break;
                                }
                            }
                        }

                        if (isGameOver) {
                            break;
                        }

                        Message moveMessage = currentPlayer.readMessage();

                        if (moveMessage.type == Type.MOVE_REQUEST) {
                            String content = moveMessage.getMessage();
                            Move move;
                            move = json.fromJson(Move.class, content);

                            boolean isValid = match.isValidMove(move);
                            System.out.println("isValid: " + isValid);
                            if (isValid) {
                                List<MoveResult> results = match.makeMove(move);
                                Message response = new Message(Type.MOVE_ACCEPT, json.prettyPrint(results));
                                currentPlayer.sendMessage(response);
                                otherPlayer.sendMessage(response);
                            } else {
                                Message notValidMessage = new Message(Type.MOVE_REJECT, "NOT VALID MOVE");
                                currentPlayer.sendMessage(notValidMessage);
                            }
                        }
                    }
                } catch (IOException e) {
                }
                isGameOver = true;
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
