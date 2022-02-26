package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 * Analyzes a move's results to determine if an extra-move has been gained.
 *
 * @author janfc
 */
public class ExtraTurnHeuristicAnalyzer extends HeuristicAnalyzer {

    @Override
    public float analyze(List<MoveResult> results) {
        float extraMove = 0;

        MatchState lastState = results.get(results.size() - 1).getState();
        MatchState firstState = results.get(0).getState();
        boolean gainedExtra = lastState.currentPlayerMove.equals(firstState.currentPlayerMove);

        extraMove = gainedExtra ? 1 : 0;

        return extraMove;
    }
}
