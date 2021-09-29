package com.janfic.games.computercombat.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class Server {

    private ServerSocket socket;
    private final Map<Integer, Client> clients;
    private final Map<String, Profile> profiles;
    private final List<MatchClient> queue;
    private final List<ServerMatchRoom> matches;
    private int MAX_MATCHES = 2;
    private Thread acceptConnections, maintainConnections, maintainQueue;

    public Server(int max_matches) {
        HeadlessFiles headlessFiles = new HeadlessFiles();
        Gdx.files = headlessFiles;
        SQLAPI.getSingleton();
        clients = new HashMap<>();
        queue = new ArrayList<>();
        matches = new ArrayList<>();
        profiles = new HashMap<>();
        this.MAX_MATCHES = max_matches;
        ServerSocketHints ssh = new ServerSocketHints();
        ssh.acceptTimeout = 5000;
        socket = Gdx.net.newServerSocket(Net.Protocol.TCP, 7272, ssh);

        acceptConnections = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("[SERVER] : Started Server on port: [7272]");
                System.out.println("[SERVER] : Listening for Connections...");

                while (true) {
                    Socket s = listen(socket);
                    if (s != null && s.isConnected()) {
                        System.out.println("[SERVER] : Found Connection");
                        accept(s);
                    }
                    //System.out.println("[SERVER] : Listening for Connections...");
                }
            }
        });
        maintainConnections = new Thread(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                AWSServices services = new AWSServices("us-east-1_pLAKB2Mxw");
                while (true) {
                    for (Integer key : clients.keySet()) {
                        Client client = clients.get(key);
                        if (client.hasMessage()) {
                            Message m = client.readMessage();
                            if (m == null) {
                                break;
                            }
                            Message r = null;
                            switch (m.type) {
                                case PING:
                                    r = new Message(Type.PING, "PINGED");
                                    break;
                                case NEW_PROFILE_REQUEST: {
                                    String content = m.getMessage();
                                    String userName = content.split(",")[0];
                                    String email = content.split(",")[1].trim();
                                    String password = content.split(",")[2];

                                    if (!services.isUsernameAvailable(userName)) {
                                        r = new Message(Type.ERROR, userName + " is not an available username");
                                    } else if (services.isEmailUsed(email)) {
                                        r = new Message(Type.ERROR, "This email is already being used");
                                    } else {
                                        String sub = services.createUser(userName, email, password);
                                        r = new Message(Type.PROFILE_INFO, sub);
                                    }
                                }
                                break;
                                case LOGIN_REQUEST: {
                                    String content = m.getMessage();
                                    String userName = content.split(",")[0];
                                    String password = content.split(",")[1];
                                    r = services.userLogin(userName, password);
                                    Profile p = SQLAPI.getSingleton()
                                            .loadProfile(r
                                                    .getMessage()
                                            );
                                    System.out.println(profiles + " " + p);
                                    profiles.put(p.getUID(), p);
                                }
                                break;
                                case VERIFICATION_CODE: {
                                    String content = m.getMessage();
                                    String username = content.split(",")[0];
                                    String code = content.split(",")[1];
                                    if (services.verifyUser(username, code)) {
                                        r = new Message(Type.SUCCESS, "User Verified");
                                    } else {
                                        r = new Message(Type.VERIFY_WITH_CODE, "Please Try Again");
                                    }
                                }
                                break;
                                case CONNECTION_REQUEST:
                                    r = new Message(Type.CONNECTION_ACCEPT, "ALREADY ACCEPTED");
                                    break;
                                case JOIN_QUEUE_REQUEST: {
                                    String content = m.getMessage();
                                    List<String> data = json.fromJson(List.class, content);
                                    Profile profile = json.fromJson(Profile.class, data.get(0));
                                    Deck deck = json.fromJson(Deck.class, data.get(1));
                                    boolean[] matchRequest = json.fromJson(boolean[].class, data.get(2));
                                    MatchClient matchClient = new MatchClient(profile, deck, client);
                                    if (queue.contains(matchClient)) {
                                        r = new Message(Type.QUEUE_POSITION, "" + (queue.indexOf(matchClient) + 1));
                                    } else {
                                        queue.add(matchClient);
                                        r = new Message(Type.QUEUE_POSITION, "" + (queue.indexOf(matchClient) + 1));
                                    }
                                }
                                break;
                                case PROFILE_INFO_REQUEST:
                                    String uid = m.getMessage();
                                    if (profiles.containsKey(uid) == false) {
                                        r = new Message(Type.PROFILE_NOT_FOUND, "NO PROFILE MATCHES GIVEN UID");
                                    } else {
                                        Profile profile = profiles.get(uid);
                                        r = new Message(Type.PROFILE_INFO, json.toJson(profile));
                                    }
                                    break;
                                case CARD_INFO_REQUEST: {
                                    String content = m.getMessage();
                                    List<String> cards = json.fromJson(List.class, content);
                                    List<Card> cardInfo = new ArrayList<>();
                                    for (String card : cards) {
                                        String data = services.getFileAsString("cards/" + card + ".json");
                                        Software c = json.fromJson(Software.class, data);
                                        cardInfo.add(c);
                                    }
                                    r = new Message(Type.CARD_INFO_RESPONSE, json.toJson(cardInfo));
                                }
                                break;
                                case UPDATE_PROFILE: {
                                    String content = m.getMessage();
                                    Profile profile = json.fromJson(Profile.class, content);
                                    services.saveProfile(profile);
                                    r = new Message(Type.SUCCESS, "PROFILE UPDATED");
                                }
                                break;
                                case CANCEL_QUEUE: {
                                    String content = m.getMessage();
                                    Profile p = profiles.get(content);
                                    boolean isRemoved = false;
                                    for (MatchClient matchClient : queue) {
                                        if (matchClient.getProfile().getUID().equals(p.getUID())) {
                                            queue.remove(matchClient);
                                            r = new Message(Type.SUCCESS, "REMOVED FROM QUEUE");
                                            isRemoved = true;
                                            break;
                                        }
                                    }
                                    if (!isRemoved) {
                                        r = new Message(Type.ERROR, "NOT IN QUEUE");
                                    }
                                }
                                break;
                                default:
                                    r = new Message(Type.ERROR, "INVALID MESSAGE TYPE");
                                    break;
                            }
                            if (r != null) {
                                try {
                                    client.sendMessage(r);
                                } catch (IOException e) {
                                    clients.remove(client.getClientUID());
                                }
                            }
                            System.out.println(r);
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        maintainQueue = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (matches.size() < MAX_MATCHES && queue.size() > 1) {
                        MatchClient a = queue.get(0);
                        MatchClient b = queue.get(1);
                        ServerMatchRoom room = new ServerMatchRoom(a, b);
                        queue.remove(a);
                        queue.remove(b);
                        clients.remove(a.getClientUID());
                        clients.remove(b.getClientUID());
                        matches.add(room);
                        room.start();

                        assert ((queue.contains(a) || queue.contains(b)) == false);
                    }

                    List<MatchClient> removed = new ArrayList<>();

                    for (int i = 0; i < queue.size(); i++) {
                        MatchClient c = queue.get(i);
                        if (c.getSocket().isConnected()) {
                            try {
                                c.sendMessage(new Message(Type.QUEUE_POSITION, "" + (i + 1)));
                            } catch (IOException e) {
                                removed.add(c);
                            }
                        } else {
                            c.getSocket().dispose();
                            removed.add(c);
                        }
                    }

                    queue.removeAll(removed);

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        Thread maintainMatches = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    List<ServerMatchRoom> r = new ArrayList<>();
                    for (ServerMatchRoom match : matches) {
                        if (match.isIsGameOver()) {
                            r.add(match);
                        }
                    }

                    for (ServerMatchRoom serverMatchRoom : r) {
                        MatchClient c1 = serverMatchRoom.getPlayer1();
                        MatchClient c2 = serverMatchRoom.getPlayer2();

                        clients.put(c1.getClientUID(), c1);
                        clients.put(c2.getClientUID(), c2);
                    }
                }
            }
        });
    }

    public void start() {
        acceptConnections.start();
        maintainConnections.start();
        maintainQueue.start();
    }

    public static void main(String[] args) {
        Gdx.net = new HeadlessNet(new HeadlessApplicationConfiguration());
        Server server = new Server(1);
        server.start();
    }

    public static Socket listen(ServerSocket ss) {
        try {
            Socket s = ss.accept(null);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Accepts a connection to a client
     *
     * @param s
     */
    public void accept(Socket s) {
        InputStream is = s.getInputStream();
        try {
            while (is.available() == 0 && s.isConnected()) {
            }
        } catch (Exception e) {
        }
        Message m = readMessage(is);
        if (m != null) {
            if (m.type == Type.CONNECTION_REQUEST) {
                Client client = new Client(s);
                while (clients.containsKey(client.getClientUID())) {
                    if (client.getIP().equals(clients.get(client.getClientUID()).getIP())) {
                        break;
                    } else {
                        client.setClientUID(client.getClientUID() + 1);
                    }
                }
                clients.put(client.getClientUID(), client);
                Message r = new Message(Type.CONNECTION_ACCEPT, "SERVER UID:" + client.getClientUID());
                try {
                    client.sendMessage(r);
                } catch (IOException e) {

                }
            }
        }
    }

    public static Message readMessage(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String content = "";
            String line;
            while (is.available() > 0 && (line = reader.readLine()) != null) {
                if (line.equals("END")) {
                    break;
                }
                content += line + "\n";
            }
            Json json = new Json();
            Message m = json.fromJson(Message.class, content);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
