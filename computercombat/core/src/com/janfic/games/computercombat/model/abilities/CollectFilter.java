package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.moves.Move;

/**
 *
 * @author Jan Fic
 */
public interface CollectFilter {
    public boolean filter(MatchState state, Move move, Component c);
}
