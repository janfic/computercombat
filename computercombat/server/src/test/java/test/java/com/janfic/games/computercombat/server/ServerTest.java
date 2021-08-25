package test.java.test.java.com.janfic.games.computercombat.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.server.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jan Fic
 */
public class ServerTest {

    Server server;

    @Before
    public void setup() {
        Gdx.net = new HeadlessNet(new HeadlessApplicationConfiguration());
        server = new Server(2);
        server.start();
    }

    @Test
    public void testClientConnection() {

        SocketHints sh = new SocketHints();
        sh.connectTimeout = 5000;
        Socket socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "localhost", 7272, sh);

        Queue<Message> messages = new LinkedList<>();
        List<Type> expectedComms = new ArrayList<>();

        messages.add(new Message(Type.CONNECTION_REQUEST, "Hi"));
        expectedComms.add(Type.CONNECTION_ACCEPT);
        messages.add(new Message(Type.NEW_PROFILE_REQUEST, "Jan Fic"));
        expectedComms.add(Type.PROFILE_INFO);
        expectedComms.add(Type.PROFILE_INFO);
        expectedComms.add(Type.PROFILE_NOT_FOUND);
        expectedComms.add(Type.ERROR);
        expectedComms.add(Type.PING);
        expectedComms.add(Type.QUEUE_POSITION);

        
        Json json = new Json();
        Profile profile = null;
        int clientUID = 0;
        try {
            while (!messages.isEmpty()) {
                Message m = messages.poll();
                String content = json.toJson(m) + "\nEND";
                socket.getOutputStream().write(content.getBytes());
                //Wait for response
                while (socket.getInputStream().available() == 0) {
                }

                Message response = Server.readMessage(socket.getInputStream());
                if (response.getType() == Type.CONNECTION_ACCEPT) {
                    clientUID = Integer.parseInt(response.getMessage().split(":")[1]);
                }
                
                System.out.println("[CLIENT](UID: " + clientUID + "): Recieved Message: { " + response.getType() + " : " + response.getMessage() + " }");
                assert (response.getType() == expectedComms.get(0));
                expectedComms.remove(0);

                
                if (response.getType() == Type.PROFILE_INFO && messages.isEmpty()) {
                    profile = json.fromJson(Profile.class, response.getMessage());
                    messages.add(new Message(Type.PROFILE_INFO_REQUEST, "" + profile.getUID()));
                    messages.add(new Message(Type.PROFILE_INFO_REQUEST, "" + 30));
                    messages.add(new Message(Type.PROFILE_INFO_REQUEST, "THIS SHOULD FAIL"));
                    messages.add(new Message(Type.PING, "PING"));
                    messages.add(new Message(Type.JOIN_QUEUE_REQUEST, ""));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            assert (false);
        }

    }
}
