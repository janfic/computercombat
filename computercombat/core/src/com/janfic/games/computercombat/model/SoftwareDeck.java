package com.janfic.games.computercombat.model;

import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class SoftwareDeck extends Card {

    public SoftwareDeck() {
        super("", "", "", 1, 0, 0, 0, 0, new Class[]{}, 0, null);
    }

    @Override
    public void beginMatch(MatchState state) {
    }

    @Override
    public void newTurn(MatchState state) {
    }

}
