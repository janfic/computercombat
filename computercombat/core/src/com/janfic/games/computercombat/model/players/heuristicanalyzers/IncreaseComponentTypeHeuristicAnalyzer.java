package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.ComponentFilter;
import java.util.List;

/**
 *
 * @author janfc
 */
public class IncreaseComponentTypeHeuristicAnalyzer extends HeuristicAnalyzer {

    int color;

    public IncreaseComponentTypeHeuristicAnalyzer(int color) {
        this.color = color;
    }

    @Override
    public float analyze(List<MoveResult> results) {
        float r = 0;
        MoveResult start = results.get(0);
        MoveResult end = results.get(results.size() - 1);

        ComponentFilter filter = new ComponentFilter() {
            @Override
            public boolean filter(Component component, MatchState state, Move move) {
                return component.getColor() == color;
            }
        };

        int endAmount = end.getState().countComponents(filter, end.getMove());
        int startAmount = start.getState().countComponents(filter, start.getMove());

        r = Math.min(Math.max(0, (endAmount - startAmount) / 3f), 1);
        return r;
    }

}
