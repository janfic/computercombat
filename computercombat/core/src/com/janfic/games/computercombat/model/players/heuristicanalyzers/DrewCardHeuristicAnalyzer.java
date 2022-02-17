package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.animations.DrawAnimation;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 * Analyzes a move's results to determine if a card has been drawn.
 *
 * @author janfc
 */
public class DrewCardHeuristicAnalyzer extends HeuristicAnalyzer {

    @Override
    public float analyze(List<MoveResult> results) {
        float drewCard = 0;

        for (MoveResult result: results) {
            List<MoveAnimation> animations = result.getAnimations();
            for (MoveAnimation animation: animations) {
                if (animation instanceof DrawAnimation) {
                    drewCard = 1;
                }
            }
        }

        return drewCard;
    }
}
