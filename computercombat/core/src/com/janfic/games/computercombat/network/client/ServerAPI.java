package com.janfic.games.computercombat.network.client;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.network.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
    Queue<Message> messages;
    Thread thread;
    boolean stop;

    public ServerAPI(Socket socket) {
        this.socket = socket;
        this.scanner = new Scanner(socket.getInputStream());
        this.messages = new LinkedList<>();
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                while (stop == false) {
                    if (dataAvailable()) {
                        Message m = readStream();
                        messages.add(m);
                        System.out.println("RECIEVED MESSAGE\nSIZE: " + json.toJson(m).length());
                    }
                    try {
                        this.wait(1000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        thread.start();
    }

    public void sendMessage(Message message) {
        try {
            Json json = new Json();
            //System.out.println(message.type);
//            System.out.println("[CLIENT]: Sending Message to Server (" + this.clientUID + ") : { " + message.getType() + " : " + message.getMessage() + " }");
            String m = json.toJson(message) + "\nEND\n";
            socket.getOutputStream().write(m.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean dataAvailable() {
        try {
            return socket.getInputStream().available() > 0 && socket.isConnected();
        } catch (IOException ex) {
            return false;
        }
    }

    private Message readStream() {
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
            return m;
        } catch (Exception e) {
            System.out.println("CONTENT: " + content);
            System.out.println("LINE: " + line);
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasMessage() {
        return !messages.isEmpty();
    }

    public Message readMessage() {
        Message m = messages.poll();
        return m;
    }

    public Message peekMessage() {
        Message m = messages.peek();
        return m;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public Socket getSocket() {
        return socket;
    }

    public void dispose() {
        stop = true;
    }
}
