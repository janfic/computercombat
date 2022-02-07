package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.network.server.MatchClient;

/**
 *
 * @author Jan Fic
 */
public class HumanPlayer extends Player {

    // Make Player Serializable
    
    private MatchClient client;

    public HumanPlayer(String uid, Deck activeDeck, Computer computer) {
        super(uid, activeDeck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        // Build Message to Send to Client
        
        // Send message
    }

    @Override
    public Move getMove(MatchState state) {
        // Build Message to Send to Client
        
        // Send message
        
        // Expect Message in Return
        
        // Return Retrieved and Deserialized Move
        return null;
    }

    public HumanPlayer() {
        super(null, null);
    }

    public void setClient(MatchClient client) {
        this.client = client;
    }
}
