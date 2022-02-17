package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;

import java.util.List;

/**
 * Analyzes a move's results to determine if an ability was used.
 *
 * @author janfc
 */
public class UseAbilityHeuristicAnalyzer extends HeuristicAnalyzer {

    @Override
    public float analyze(List<MoveResult> results) {

        for (MoveResult result: results) {
            if (result.getMove() instanceof UseAbilityMove) return 1;
        }

        return 0;
    }
}
