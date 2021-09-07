package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.SoftwareDeck;

/**
 *
 * @author Jan Fic
 */
public class HumanPlayer extends Player {

    public String uid;
    
    public HumanPlayer(String uid, SoftwareDeck activeDeck, Computer computer) {
        super(uid, activeDeck, computer);
        this.uid = uid;
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
    }

    @Override
    public Move getMove(MatchState state) {
        return null;
    }

}
