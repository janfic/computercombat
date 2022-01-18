package com.janfic.games.computercombat.network.client;

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

}
