package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.match.MatchState;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class SoftwareDeck extends Card {

    public SoftwareDeck() {
        super(1, "", "", "", 1, 0, 0, 0, 0, new Class[]{}, 0, null);
    }

    @Override
    public void beginMatch(MatchState state) {
    }

    @Override
    public void newTurn(MatchState state) {
    }

}
