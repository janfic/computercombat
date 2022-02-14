package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 *
 * @author janfc
 */
public abstract class HeuristicAnalyzer {

    public abstract float analyze(List<MoveResult> results);
}
