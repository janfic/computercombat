package com.janfic.games.computercombat.model.players;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.server.MatchClient;
import com.janfic.games.computercombat.util.ObjectMapSerializer;

/**
 *
 * @author Jan Fic
 */
public class HumanPlayer extends Player {

    // Make Player Serializable
    private MatchClient client;
    Json json;

    public HumanPlayer(String uid, Deck activeDeck, Computer computer) {
        super(uid, activeDeck);
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
    public Move getMove(MatchState state) {
        // Build Message to Send to Client
        Message message = new Message(Type.MATCH_STATE_DATA, json.toJson(state));
        // Send message
        try {
            client.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Expect Message in Return
        while (client.hasMessage() == false && client.getSocket().isConnected()) {
            //time out code
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

    public HumanPlayer() {
        super(null, null);
    }

    public void setClient(MatchClient client) {
        this.client = client;
    }
}
