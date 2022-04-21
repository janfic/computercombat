package com.janfic.games.computercombat.model.match;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.abilities.DrawAbility;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.Move;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class Match {
    
    private MatchState currentState;
    
    public Match(Player player1, Player player2) {
        
        Map<String, List<Card>> activeEntities = new HashMap<>();
        Map<String, Card> computers = new HashMap<>();
        Map<String, Deck> decks = new HashMap<>();
        
        player1.getActiveDeck().shuffle();
        player2.getActiveDeck().shuffle();
        decks.put(player1.getUID(), player1.getActiveDeck());
        decks.put(player2.getUID(), player2.getActiveDeck());
        
        activeEntities.put(player1.getUID(), new ArrayList<>());
        activeEntities.put(player2.getUID(), new ArrayList<>());
        computers.put(player1.getUID(), Card.makeComputer(player1.getUID()));
        computers.put(player2.getUID(), Card.makeComputer(player2.getUID()));
        
        try {
            this.currentState = new MatchState(player1, player2, makeBoard(GameRules.componentFrequencies), activeEntities, computers, decks);
            this.currentState.update();
            while (currentState.getMatches().isEmpty() == false) {
                this.currentState = new MatchState(player1, player2, makeBoard(GameRules.componentFrequencies), activeEntities, computers, decks);
                this.currentState.update();
            }
            for (int i = 0; i < 3; i++) {
                Card c = decks.get(player1.getUID()).draw();
                c.setOwnerUID(player1.getUID());
                Card b = decks.get(player2.getUID()).draw();
                b.setOwnerUID(player2.getUID());
                this.currentState.activeEntities.get(player1.getUID()).add(c);
                this.currentState.activeEntities.get(player2.getUID()).add(b);
            }
            
        } catch (Exception e) {
            System.err.println("Something went wrong when creating the initial match state: ");
            e.printStackTrace();
        }
    }
    
    private Component[][] makeBoard(Map<Integer, Integer> colorFrequencies) throws Exception {
        Component[][] componentBoard = new Component[8][8];

        // Construct Component Frequencies
        List<Integer> colorBag = new ArrayList<>();
        for (Integer type : colorFrequencies.keySet()) {
            int frequency = colorFrequencies.get(type);
            colorBag.addAll(Collections.nCopies(frequency, type));
        }

        // Generate Board
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                int color = colorBag.get((int) (Math.random() * colorBag.size()));
                Component c = new Component(color, x, y);
                componentBoard[x][y] = c;
                c.invalidate();
            }
        }

        //Set Neighbors
        MatchState.buildNeighbors(componentBoard);
        
        return componentBoard;
    }
    
    public MatchState getPlayerMatchState(String playerUID) {
        MatchState copy = currentState.clone(currentState, playerUID);
        MatchState.buildNeighbors(copy.getComponentBoard());
        currentState.update();
        return copy;
    }
    
    public MatchState getCurrentState() {
        return currentState;
    }
    
    public List<MoveResult> makeMove(Move move) {
        List<MoveResult> r = GameRules.makeMove(currentState, move);
        return r;
    }
    
    public String whosMove() {
        return currentState.currentPlayerMove.getUID();
    }
    
    public boolean isValidMove(Move move) {
        List<Move> moves = GameRules.getAvailableMoves(currentState);
        boolean found = moves.contains(move);
        Json j = new Json();
        
        return found;
    }
}
