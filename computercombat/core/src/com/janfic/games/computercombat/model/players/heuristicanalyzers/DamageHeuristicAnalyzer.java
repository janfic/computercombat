package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 *
 * @author janfc
 */
public class DamageHeuristicAnalyzer extends HeuristicAnalyzer {

    final Integer GOOD_AMOUNT_OF_DAMAGE = 4;

    @Override
    public float analyze(List<MoveResult> results) {
        float r = 0;

        MoveResult before = results.get(0);
        MoveResult end = results.get(results.size() - 1);
        float beforeHealth = getTotalHealths(before);
        float endHealth = getTotalHealths(end);

        //r = (beforeHealth - endHealth) / getMaxHealth(before);
        r = Math.min((beforeHealth - endHealth) / GOOD_AMOUNT_OF_DAMAGE * results.size(), 1);

        return r;
    }

    public int getTotalHealths(MoveResult result) {
        int totalHealth = 0;
        String currentUID = result.getMove().getPlayerUID();
        String opponentUID = result.getState().getOtherProfile(currentUID).getUID();
        for (Card card : result.getState().activeEntities.get(opponentUID)) {
            totalHealth += card.getHealth();
            totalHealth += card.getArmor();
        }
        totalHealth += result.getState().computers.get(opponentUID).getHealth();
        return totalHealth;
    }

    /*
    public int getMaxHealth(MoveResult result) {
        int totalHealth = 0;
        String currentUID = result.getMove().getPlayerUID();
        String opponentUID = result.getState().getOtherProfile(currentUID).getUID();
        for (Card card : result.getState().activeEntities.get(opponentUID)) {
            totalHealth += card.getMaxArmor();
            totalHealth += card.getMaxHealth();
        }
        totalHealth += result.getState().computers.get(opponentUID).getMaxHealth();
        return totalHealth;
    }
     */
}
