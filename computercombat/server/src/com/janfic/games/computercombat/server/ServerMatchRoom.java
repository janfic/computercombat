package com.janfic.games.computercombat.server;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Match;
import com.janfic.games.computercombat.model.Move;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.io.IOException;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom extends Thread {

    private final MatchClient player1, player2;
    private Match match;

    public ServerMatchRoom(MatchClient player1, MatchClient player2) {
        super(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message1 = new Message(Type.FOUND_MATCH, player2.getProfile().getName());
                    Message message2 = new Message(Type.FOUND_MATCH, player1.getProfile().getName());

                    player1.sendMessage(message1);
                    player2.sendMessage(message2);

                    while (player1.hasMessage() == false) {
                    }
                    while (player2.hasMessage() == false) {
                    }

                    Message response1 = player1.readMessage();
                    Message response2 = player2.readMessage();

                    if (response1.type != Type.READY || response2.type != Type.READY) {
                        Message error = new Message(Type.ERROR, "A PLAYER WASNT READY");
                        player1.sendMessage(error);
                        player2.sendMessage(error);
                    }

                    Json json = new Json();
                    Match match = new Match(player1.getProfile(), player2.getProfile());
                    Message matchData1 = new Message(Type.MATCH_STATE_DATA, json.toJson(match.getPlayerMatchState(player1.getProfile().getUID())));
                    Message matchData2 = new Message(Type.MATCH_STATE_DATA, json.toJson(match.getPlayerMatchState(player2.getProfile().getUID())));

                    player1.sendMessage(matchData1);
                    player2.sendMessage(matchData2);

                    while (true) {
                        String currentPlayersMove = match.whosMove();
                        MatchClient currentPlayer = player1.getProfile().getUID().equals(currentPlayersMove) ? player1 : player2;

                        while (currentPlayer.hasMessage() == false) {
                        }

                        Message moveMessage = currentPlayer.readMessage();

                        if (moveMessage.type == Type.MOVE_REQUEST) {
                            String content = moveMessage.getMessage();
                            Move move = json.fromJson(Move.class, content);

                            boolean isValid = match.isValidMove(move);
                            if (isValid) {
                                
                            } else {
                                Message notValidMessage = new Message(Type.MOVE_REJECT, "NOT VALID MOVE");
                                currentPlayer.sendMessage(notValidMessage);
                            }
                        }
                    }

                } catch (IOException e) {

                }
            }
        });
        this.player1 = player1;
        this.player2 = player2;

    }
}
