package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.match.MatchState;

/**
 *
 * @author Jan Fic
 */
public interface Trait {

    public void beginMatch(MatchState state);

    public void newTurn(MatchState state);
}
