package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

public class CompletelyChargeAbilitiesHeuristicAnalyzer extends HeuristicAnalyzer {

    @Override
    public float analyze(List<MoveResult> results) {
        float r = 0;

        MoveResult before = results.get(0);
        MoveResult end = results.get(results.size() - 1);

        String currentUID = before.getMove().getPlayerUID();
        List<Card> cards = end.getState().activeEntities.get(currentUID);

        List<Card> beforeCards = before.getState().activeEntities.get(currentUID);

        for (Card card: cards) {
            if (card.getRunProgress() == card.getRunRequirements()) {
                // this is fully charged. was it charged before?
                before.getState().activeEntities.get(currentUID);
                for (Card beforeCard: beforeCards) {
                    if (beforeCard.getMatchID() == card.getMatchID() && beforeCard.getRunProgress() < beforeCard.getRunRequirements()) {
                        r = 1;
                        break;
                    }
                }
            }
        }

        return r;
    }
}
