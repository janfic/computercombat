package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;

/**
 *
 * @author Jan Fic
 */
public class HumanPlayer extends Player {

    public String uid;

    public HumanPlayer(String uid, Deck activeDeck, Computer computer) {
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

    public HumanPlayer() {
        super(null, null, null);
    }

}
