package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.ComponentFilter;
import java.util.List;

/**
 *
 * @author janfic
 */
public class KeepComponentTypeHeuristicAnalyzer extends HeuristicAnalyzer {

    int color;

    public KeepComponentTypeHeuristicAnalyzer(int color) {
        this.color = color;
    }

    @Override
    public float analyze(List<MoveResult> results) {
        float r = 0;
        MoveResult end = results.get(results.size() - 1);

        ComponentFilter filter = new ComponentFilter() {
            @Override
            public boolean filter(Component component, MatchState state, Move move) {
                return component.getColor() == color;
            }
        };

        int amount = end.getState().countComponents(filter, end.getMove());

        r = Math.min(amount / 25f, 1);
        return r;
    }
}
