package com.janfic.games.computercombat.model.players;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.server.MatchClient;
import com.janfic.games.computercombat.util.ObjectMapSerializer;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class HumanPlayer extends Player {

    // Make Player Serializable
    private MatchClient client;
    Json json;

    public HumanPlayer() {
    }

    public HumanPlayer(String uid, MatchClient client) {
        super(uid, client.getDeck());
        this.client = client;
        this.json = new Json();
        json.setSerializer(ObjectMap.class, new ObjectMapSerializer());
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        // Build Message to Send to Client
        Message message = new Message(Type.MATCH_STATE_DATA, json.toJson(state));

        // Send message
        try {
            client.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move getMove() {
        // Expect Message in Return
        float timeStart = System.nanoTime();
        float delta = 0;
        while (client.hasMessage() == false && client.getSocket().isConnected()) {
            delta = System.nanoTime() - timeStart;
            if (delta / 1000000000f >= 10) {
                try {
                    client.sendMessage(new Message(Type.PING, "PING"));
                    System.out.println("PINGED CLIENT");
                    timeStart = System.nanoTime();
                } catch (Exception e) {
                    return null;
                }
            }
        }

        // Return Retrieved and Deserialized Move
        Message m = client.readMessage();
        if (m.getType() == Type.MOVE_REQUEST) {
            Move move = json.fromJson(Move.class, m.getMessage());
            return move;
        } else {
            return null;
        }

        // if method returns null, player forfiets?
    }

    @Override
    public void updateState(List<MoveResult> state) {
        // Build Message to Send to Client
        Message message = new Message(Type.MOVE_ACCEPT, json.toJson(state));
        // Send message
        try {
            client.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameOver(MatchResults results) {

    }

}
