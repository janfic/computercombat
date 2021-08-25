package com.janfic.games.computercombat.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom extends Thread {

    private ServerSocket socket;
    private Client player1, player2;

    public ServerMatchRoom(Client player1, Client player2) {
        super(new Runnable() {
            @Override
            public void run() {

            }
        });
        this.player1 = player1;
        this.player2 = player2;
    }
}
