package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;

/**
 *
 * @author Jan Fic
 */
public interface StateAnalyzer<T> {

    public T analyze(MatchState state, Move move);
}
