package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.animations.CollectAnimation;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 * Analyzes a move's results to evaluate it based on the number of components collected.
 *
 * @author janfc
 */
public class ComponentsCollectedHeuristicAnalyzer extends HeuristicAnalyzer {

    final Integer GOOD_NUMBER_OF_COMPONENTS = 5;

    @Override
    public float analyze(List<MoveResult> results) {
        float maxValue = results.size() * GOOD_NUMBER_OF_COMPONENTS;

        Integer totalComponentsCollected = 0;
        for (MoveResult result: results) {
            List<MoveAnimation> animations = result.getAnimations();
            for (MoveAnimation animation: animations) {
                if (animation instanceof CollectAnimation) {
                    CollectAnimation collectAnimation = (CollectAnimation) animation;
                    Integer componentsCollected = collectAnimation.getAllComponents().size();
                    totalComponentsCollected += componentsCollected;
                }
            }
        }

        return Math.min((totalComponentsCollected / maxValue), 1);
    }
}
