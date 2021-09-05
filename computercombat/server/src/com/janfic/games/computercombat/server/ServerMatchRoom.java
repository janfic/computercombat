package com.janfic.games.computercombat.server;

import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom extends Thread {

    private final MatchClient player1, player2;

    public ServerMatchRoom(MatchClient player1, MatchClient player2) {
        super(new Runnable() {
            @Override
            public void run() {
                Message message1 = new Message(Type.FOUND_MATCH, player2.getProfile().getName());
                Message message2 = new Message(Type.FOUND_MATCH, player1.getProfile().getName());

                try {
                    player1.sendMessage(message1);
                    player2.sendMessage(message2);
                } catch (IOException e) {

                }
            }
        });
        this.player1 = player1;
        this.player2 = player2;
    }
}
