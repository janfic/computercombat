package com.janfic.games.computercombat.network.client;

import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;
import java.util.ArrayList;
import java.util.List;

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
     * Initialize the match with a state. The state given will not contain
     * serialized information about cards, so querying them from the database is
     * required.
     *
     * @param beginState
     */
    public void initializeState(MatchState beginState) {
        this.currentState = beginState;
        // Cards
        for (String string : currentState.activeEntities.keySet()) {
            List<Card> cards = currentState.activeEntities.get(string);
            List<Card> loadedCards = new ArrayList<>();
            for (Card card : currentState.activeEntities.get(string)) {
                Card c = SQLAPI.getSingleton().getCardById(card.getID(), card.getOwnerUID());
                c.setMatchID(card.getMatchID());
                c.setHealth(card.getHealth());
                c.setArmor(card.getArmor());
                c.setAttack(card.getAttack());
                c.setProgress(card.getRunProgress());
                loadedCards.add(c);
            }
            cards.clear();
            cards.addAll(loadedCards);
        }
        // Computers
        for (String string : currentState.computers.keySet()) {
            Card c = currentState.computers.get(string);
            Card loadedComputer = Card.makeComputer(string);
            loadedComputer.setMatchID(c.getMatchID());
            loadedComputer.setHealth(c.getHealth());
            loadedComputer.setArmor(c.getArmor());
            loadedComputer.setAttack(c.getAttack());
            loadedComputer.setProgress(c.getRunProgress());
            currentState.computers.put(string, loadedComputer);
        }
    }

    /**
     * Update the current state given information about what has changed. Assume
     * that all other values have stayed the same.
     *
     * @param deltaState Contains only information about what has changed, not a
     * complete new state. ie. null values and zeros
     */
    public void updateState(MatchState deltaState) {
        // Check for Card Changes
        boolean cardChanges = false;
        for (String string : currentState.activeEntities.keySet()) {
            if (currentState.activeEntities.get(string).size() != deltaState.activeEntities.get(string).size()) {
                cardChanges = true;

            }
        }
        // Cards
        if (cardChanges == false) {
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
        } else {
            initializeState(deltaState);
        }
        // Board
        this.currentState.componentBoard = deltaState.componentBoard;
        // Current Player Move
        this.currentState.currentPlayerMove = deltaState.currentPlayerMove;
        // Decks
        this.currentState.decks = deltaState.decks;
        // Computers
        for (String string : currentState.computers.keySet()) {
            Card computer = deltaState.computers.get(string);
            Card updatedComputer = currentState.computers.get(string);
            computer.setHealth(updatedComputer.getHealth());
            computer.setArmor(updatedComputer.getArmor());
            computer.setAttack(updatedComputer.getAttack());
            computer.setProgress(updatedComputer.getRunProgress());
        }
    }
}
