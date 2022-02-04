package com.janfic.games.computercombat.util;

import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;

/**
 *
 * @author Jan Fic
 */
public interface Filter<T> {

    public boolean filter(T a, MatchState state, Move move);

    public String getDescription();
}
