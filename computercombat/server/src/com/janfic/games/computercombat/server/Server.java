package com.janfic.games.computercombat.server;

import com.janfic.games.computercombat.network.server.MatchClient;
import com.janfic.games.computercombat.network.server.Client;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.headless.*;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.*;
import com.janfic.games.computercombat.model.players.HeuristicBotPlayer;
import com.janfic.games.computercombat.model.players.HumanPlayer;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.util.NullifyingJson;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

/**
 *
 * @author Jan Fic
 */
public class Server {

    private ServerSocket socket;
    private final Map<Integer, Client> clients;
    private final Map<String, Profile> profiles;
    private final List<MatchClient> liveQueue;
    private final List<MatchClient> raidQueue;
    private final List<ServerMatchRoom> matches;
    private final List<MatchClient> clientsInMatch;
    private int MAX_MATCHES = 2;
    private Thread acceptConnections, maintainConnections, maintainLiveQueue, maintainRaidQueue, maintainMatches;

    public Server(int max_matches) {
        HeadlessFiles headlessFiles = new HeadlessFiles();
        Gdx.files = headlessFiles;
        SQLAPI.getSingleton();
        clients = new HashMap<>();
        liveQueue = new ArrayList<>();
        raidQueue = new ArrayList<>();
        matches = new ArrayList<>();
        clientsInMatch = new ArrayList<>();
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
                Json json = new NullifyingJson();
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

                                    try {

                                        String sub = services.createUser(userName, email, password);
                                        r = new Message(Type.PROFILE_INFO, sub);
                                    } catch (UsernameExistsException e) {
                                        r = new Message(Type.ERROR, "The username" + userName + " is unavailable.");
                                    } catch (Exception e) {
                                        r = new Message(Type.ERROR, "An unknown error occured.");
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
                                    System.out.println(data);
                                    Profile profile = json.fromJson(Profile.class, data.get(0));
                                    Deck deck = json.fromJson(Deck.class, data.get(1));
                                    boolean[] queuePreferences = json.fromJson(boolean[].class, data.get(2));
                                    MatchClient matchClient = new MatchClient(profile, deck, client);
                                    List<MatchClient> queue = queuePreferences[1] ? liveQueue : raidQueue;
                                    if (!queue.contains(matchClient)) {
                                        queue.add(matchClient);
                                    }
                                    r = new Message(Type.QUEUE_POSITION, "" + (queue.indexOf(matchClient) + 1));

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
                                        Card c = json.fromJson(Card.class, data);
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
                                    for (MatchClient matchClient : liveQueue) {
                                        if (matchClient.getProfile().getUID().equals(p.getUID())) {
                                            liveQueue.remove(matchClient);
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
        maintainLiveQueue = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (matches.size() < MAX_MATCHES && liveQueue.size() > 1) {
                        MatchClient a = liveQueue.get(0);
                        MatchClient b = liveQueue.get(1);
                        Player playerA = new HumanPlayer(a.getProfile().getUID(), a);
                        Player playerB = new HumanPlayer(b.getProfile().getUID(), b);
                        ServerMatchRoom room = new ServerMatchRoom(playerA, playerB);

                        try {
                            Message message1 = new Message(Type.FOUND_MATCH, a.getProfile().getName());
                            Message message2 = new Message(Type.FOUND_MATCH, b.getProfile().getName());

                            a.sendMessage(message2);
                            b.sendMessage(message1);

                            // Runs Asyncroniously
                            CompletableFuture.runAsync(() -> {
                                while (!a.hasMessage() || !b.hasMessage()) {
                                }
                                Message responseA = a.readMessage();
                                Message responseB = b.readMessage();

                                try {
                                    Message error = new Message(Type.ERROR, "A PLAYER WASNT READY");
                                    Message leftQUEUE = new Message(Type.SUCCESS, "LEFT QUEUE");

                                    if (responseA.type == Type.CANCEL_QUEUE) {
                                        a.sendMessage(leftQUEUE);
                                        b.sendMessage(error);
                                    }
                                    if (responseB.type == Type.CANCEL_QUEUE) {
                                        a.sendMessage(error);
                                        b.sendMessage(leftQUEUE);
                                    }
                                } catch (Exception e) {
                                }

                                clientsInMatch.add(a);
                                clientsInMatch.add(b);
                                matches.add(room);
                                room.start();
                            });

                            liveQueue.remove(a);
                            liveQueue.remove(b);
                            clients.remove(a.getClientUID());
                            clients.remove(b.getClientUID());
                        } catch (Exception e) {
                        }
                    }

                    List<MatchClient> removed = new ArrayList<>();

                    for (int i = 0; i < liveQueue.size(); i++) {
                        MatchClient c = liveQueue.get(i);
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

                    liveQueue.removeAll(removed);

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        maintainRaidQueue = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (matches.size() < MAX_MATCHES && raidQueue.size() > 0) {
                        try {
                            MatchClient a = raidQueue.get(0);
                            Player playerA = new HumanPlayer(a.getProfile().getUID(), a);
                            Player botPlayer = new HeuristicBotPlayer("botUID", (Deck) a.getDeck().clone());
                            ServerMatchRoom room = new ServerMatchRoom(playerA, botPlayer);
                            Message message2 = new Message(Type.FOUND_MATCH, "RAID BOT");
                            a.sendMessage(message2);
                            // Runs Asyncroniously
                            CompletableFuture.runAsync(() -> {
                                while (!a.hasMessage()) {
                                }
                                Message responseA = a.readMessage();
                                try {
                                    Message leftQUEUE = new Message(Type.SUCCESS, "LEFT QUEUE");

                                    if (responseA.type == Type.CANCEL_QUEUE) {
                                        a.sendMessage(leftQUEUE);
                                    }
                                } catch (Exception e) {
                                }

                                clientsInMatch.add(a);
                                matches.add(room);
                                room.start();
                            });

                            raidQueue.remove(a);
                            clients.remove(a.getClientUID());
                        } catch (Exception e) {
                        }
                    }

                    List<MatchClient> removed = new ArrayList<>();

                    for (int i = 0; i < raidQueue.size(); i++) {
                        MatchClient c = raidQueue.get(i);
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

                    raidQueue.removeAll(removed);

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {

                    }
                }
            }

        });
        maintainMatches = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Ping database to prevent timeout
                    SQLAPI.getSingleton().pingDatabase();

                    List<ServerMatchRoom> r = new ArrayList<>();
                    for (ServerMatchRoom match : matches) {
                        if (match.isGameOver()) {
                            r.add(match);
                        }
                    }

                    for (ServerMatchRoom serverMatchRoom : r) {
                        Player player1 = serverMatchRoom.getPlayer1();
                        Player player2 = serverMatchRoom.getPlayer2();

                        for (MatchClient matchClient : clientsInMatch) {
                            String clientUID = matchClient.getProfile().getUID();
                            if (clientUID.equals(player1.getUID()) || clientUID.equals(player2.getUID())) {
                                clients.put(matchClient.getClientUID(), matchClient);
                            }
                        }
                    }
                    matches.removeAll(r);

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    public void start() {
        acceptConnections.start();
        maintainConnections.start();
        maintainLiveQueue.start();
        maintainRaidQueue.start();
        maintainMatches.start();
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

    public Player getDefensivePlayer() {
        // TODO: Retrieve Defensive Player from Randomly chosen profile
        return null;
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
