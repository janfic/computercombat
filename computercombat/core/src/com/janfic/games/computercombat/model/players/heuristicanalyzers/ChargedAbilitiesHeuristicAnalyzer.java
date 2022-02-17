package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;
import java.util.Map;

/**
 * Analyzes a move's results to determine how many components went to charge an ability.
 *
 * @author janfc
 */
public class ChargedAbilitiesHeuristicAnalyzer extends HeuristicAnalyzer {

    @Override
    public float analyze(List<MoveResult> results) {
        MatchState oldState = results.get(0).getOldState();
        Map<String, List<Card>> activeEntities = oldState.activeEntities;

        String playerUid = results.get(0).getMove().getPlayerUID();
        List<Card> activeCards = activeEntities.get(playerUid);

        Integer oldRunProgress = 0;
        Integer totalRunRequirements = 0;
        for (Card activeCard : activeCards) {
            oldRunProgress += activeCard.getRunProgress();
            totalRunRequirements += activeCard.getRunRequirements();
        }

        MatchState endState = results.get(results.size() - 1).getNewState();
        Map<String, List<Card>> newActiveEntities = endState.activeEntities;

        List<Card> newActiveCards = newActiveEntities.get(playerUid);

        Integer newRunProgress = 0;
        for (Card newActiveCard : newActiveCards) {
            newRunProgress += newActiveCard.getRunProgress();
        }

        return Math.max(0, (newRunProgress - oldRunProgress) / Math.min(totalRunRequirements, 1));
    }
}
