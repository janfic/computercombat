package com.janfic.games.computercombat.model;

/**
 *
 * @author Jan Fic
 */
public abstract class Player {

    private final String uid;

    public Player(String uid, SoftwareDeck activeDeck, Computer computer) {
        this.uid = uid;
    }

    public String getUID() {
        return uid;
    }

    public abstract void beginMatch(MatchState state, Player opponent);

    public abstract Move getMove(MatchState state);
}
