package com.janfic.games.computercombat.server;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.network.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Jan Fic
 */
public class Client {

    private Socket socket;
    private int clientUID;

    public Client(Socket socket) {
        this.socket = socket;
        this.clientUID = (int) (Math.random() * Integer.MAX_VALUE);
    }

    public int getClientUID() {
        return clientUID;
    }

    public void setClientUID(int clientUID) {
        this.clientUID = clientUID;
    }

    public String getIP() {
        return socket.getRemoteAddress();
    }

    public void sendMessage(Message message) {
        try {
            Json json = new Json();
            String out = message.getMessage().length() > 50 ? message.getMessage().substring(0, 25) + " .... " + message.getMessage().substring(message.getMessage().length() - 25, message.getMessage().length() - 1) : message.getMessage();
            System.out.println("[SERVER]: Sending Message to Client (" + this.clientUID + ") : { " + message.getType() + " : " + out + " }");
            String m = json.toJson(message) + "\nEND";
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
        try {
            Json json = new Json();
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String content = "";
            String line;
            while (is.available() > 0 && (line = reader.readLine()) != null) {
                if (line.equals("END")) {
                    break;
                }
                content += line + "\n";
            }
            Message m = json.fromJson(Message.class, content);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
