package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

public class ChargeAbilitiesHeuristicAnalyzer extends HeuristicAnalyzer {

    @Override
    public float analyze(List<MoveResult> results) {
        float r = 0;

        MoveResult before = results.get(0);
        MoveResult end = results.get(results.size() - 1);

        r = (getCharges(end) - getCharges(before)) / getMaxCharges(before);
        return r;
    }

    public int getCharges(MoveResult result) {
        int totalCharge = 0;
        String currentUID = result.getMove().getPlayerUID();
        String opponentUID = result.getState().getOtherProfile(currentUID).getUID();
        for (Card card : result.getState().activeEntities.get(opponentUID)) {
            totalCharge += card.getRunProgress();
        }
        totalCharge += result.getState().computers.get(opponentUID).getRunProgress();
        return totalCharge;
    }

    public int getMaxCharges(MoveResult result) {
        int totalCharge = 0;
        String currentUID = result.getMove().getPlayerUID();
        String opponentUID = result.getState().getOtherProfile(currentUID).getUID();
        for (Card card : result.getState().activeEntities.get(opponentUID)) {
            totalCharge += card.getRunRequirements();
        }
        totalCharge += result.getState().computers.get(opponentUID).getRunProgress();
        return totalCharge;
    }
}
