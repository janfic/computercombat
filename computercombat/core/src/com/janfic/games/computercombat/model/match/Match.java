package com.janfic.games.computercombat.model.match;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
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
        Map<String, Computer> computers = new HashMap<>();
        Map<String, Deck> decks = new HashMap<>();

        player1.getActiveDeck().shuffle();
        player2.getActiveDeck().shuffle();
        decks.put(player1.getUID(), player1.getActiveDeck());
        decks.put(player2.getUID(), player2.getActiveDeck());

        activeEntities.put(player1.getUID(), new ArrayList<>());
        activeEntities.put(player2.getUID(), new ArrayList<>());
        computers.put(player1.getUID(), new Computer());
        computers.put(player2.getUID(), new Computer());

        try {
            this.currentState = new MatchState(player1, player2, makeBoard(GameRules.componentFrequencies), activeEntities, computers, decks);
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

        System.out.println("GENERATING BOARD");
        // Generate Board
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                int color = colorBag.get((int) (Math.random() * colorBag.size()));
                Component c = new Component(color, x, y);
                componentBoard[x][y] = c;
                c.invalidate();
            }
        }

        System.out.println("SETTING NEIGHBORS");
        //Set Neighbors
        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                for (int i = 0; i < Component.coordsToNeighbors.length; i += 3) {
                    int neighborX = x + Component.coordsToNeighbors[i];
                    int neighborY = y + Component.coordsToNeighbors[i + 1];
                    if (neighborX < componentBoard.length && neighborY < componentBoard[x].length && neighborX >= 0 && neighborY >= 0) {
                        Component c = componentBoard[neighborX][neighborY];
                        componentBoard[x][y].setNeighbor(Component.coordsToNeighbors[i + 2], c);
                    }
                }
            }
        }

        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                Component c = componentBoard[x][y];
                if (c.isInvalid()) {
                    c.update();
                }
            }
        }

        System.out.println("DONE SETTING NEIGHBORS");

        // Remove Matches
        while (GameRules.areAvailableComponentMatches(componentBoard).isEmpty() || !GameRules.getCurrentComponentMatches(componentBoard).isEmpty()) {
            for (int x = 0; x < componentBoard.length; x++) {
                for (int y = 0; y < componentBoard[x].length; y++) {
                    Collections.shuffle(colorBag);
                    int color = colorBag.get((int) (Math.random() * colorBag.size()));
                    Component c = new Component(color, x, y);
                    componentBoard[x][y] = c;
                }
            }
        }
        return componentBoard;
    }

    public MatchState getPlayerMatchState(String playerUID) {
        MatchState copy = new MatchState(currentState, playerUID);
        return copy;
    }

    public MatchState getCurrentState() {
        return currentState;
    }

    public List<MoveResult> makeMove(Move move) {
        List<MoveResult> r = GameRules.makeMove(currentState, move);
        this.currentState = r.get(r.size() - 1).getNewState();
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
