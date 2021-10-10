package com.janfic.games.computercombat.network.client;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.network.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author Jan Fic
 */
public class ServerAPI {

    Socket socket;
    private boolean isReading;
    private int clientUID;
    Scanner scanner;

    public ServerAPI(Socket socket) {
        this.socket = socket;
        this.scanner = new Scanner(socket.getInputStream());
    }

    public void sendMessage(Message message) {
        try {
            Json json = new Json();
//            System.out.println("[CLIENT]: Sending Message to Server (" + this.clientUID + ") : { " + message.getType() + " : " + message.getMessage() + " }");
            String m = json.toJson(message) + "\nEND\n";
            socket.getOutputStream().write(m.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasMessage() {
        try {
            return socket.getInputStream().available() > 0 && socket.isConnected();
        } catch (IOException ex) {
            return false;
        }
    }

    public Message readMessage() {
        System.out.println("READING");
        String content = "";
        String line = null;
        try {
            while (isReading) {
            }
            Json json = new Json();
            isReading = true;
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.equals("END")) {
                    break;
                }
                content += line + "\n";
            }
            Message m = json.fromJson(Message.class, content);
            isReading = false;
            System.out.println(m.type + "FINISHED READING");
            return m;
        } catch (Exception e) {
            System.out.println("FINISHED READING");
            System.out.println("CONTENT: " + content);
            System.out.println("LINE: " + line);
            e.printStackTrace();
            return null;
        }
    }

    public void update() {
        if (hasMessage()) {
            //System.out.println(readMessage());
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public Socket getSocket() {
        return socket;
    }
}
