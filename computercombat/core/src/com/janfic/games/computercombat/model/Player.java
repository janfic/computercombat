package com.janfic.games.computercombat.model;

/**
 *
 * @author Jan Fic
 */
public abstract class Player {

    private final int uid;

    public Player(int uid, SoftwareDeck activeDeck, Computer computer) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public abstract void beginMatch(MatchState state, Player opponent);

    public abstract Move getMove(MatchState state);
}
