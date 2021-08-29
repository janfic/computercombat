package com.janfic.games.computercombat.model;

/**
 *
 * @author Jan Fic
 */
public interface Trait {

    public void beginMatch(MatchState state);

    public void newTurn(MatchState state);
}
