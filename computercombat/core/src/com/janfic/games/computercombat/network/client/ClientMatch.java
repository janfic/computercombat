package com.janfic.games.computercombat.network.client;

import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;

/**
 *
 * @author Jan Fic
 */
public class ClientMatch {

    String opponentName;
    MatchState currentState;

    public ClientMatch(String opponentName) {
        this.opponentName = opponentName;
    }

    public MatchState getCurrentState() {
        return currentState;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setCurrentState(MatchState currentState) {
        this.currentState = currentState;
    }

    /**
     * Update the current state given information about what has changed. Assume
     * that all other values have stayed the same.
     *
     * @param deltaState Contains only information about what has changed, not a
     * complete new state. ie. null values and zeros
     */
    public void updateState(MatchState deltaState) {
        for (String string : currentState.activeEntities.keySet()) {
            for (Card card : currentState.activeEntities.get(string)) {
                for (Card change : deltaState.activeEntities.get(string)) {
                    if (change.getMatchID() == card.getMatchID()) {
                        card.setHealth(change.getHealth());
                        card.setArmor(change.getArmor());
                        card.setAttack(change.getAttack());
                        card.setProgress(change.getRunProgress());
                    }
                }
            }
        }
    }
}
