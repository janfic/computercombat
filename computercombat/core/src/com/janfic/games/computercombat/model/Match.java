package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
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
    private Profile player1, player2;

    public Match(Profile player1, Profile player2, Deck player1Deck, Deck player2Deck) {

        this.player1 = player1;
        this.player2 = player2;
        Map<String, List<Card>> activeEntities = new HashMap<>();
        Map<String, Computer> computers = new HashMap<>();
        Map<String, Deck> decks = new HashMap<>();

        decks.put(player1.getUID(), player1Deck);
        decks.put(player2.getUID(), player2Deck);

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

    private Component[][] makeBoard(Map<Class<? extends Component>, Integer> componentFrequencies) throws Exception {
        Component[][] componentBoard = new Component[8][8];
        List<Class<? extends Component>> components = new ArrayList<>();
        for (Class<? extends Component> type : componentFrequencies.keySet()) {
            int frequency = componentFrequencies.get(type);
            components.addAll(Collections.nCopies(frequency, type));
        }

        for (int x = 0; x < componentBoard.length; x++) {
            for (int y = 0; y < componentBoard[x].length; y++) {
                Collections.shuffle(components);
                Class<? extends Component> component = components.get(0);
                Component c = component.getConstructor(int.class, int.class).newInstance(x, y);
                componentBoard[x][y] = c;
            }
        }

        while (GameRules.areAvailableComponentMatches(componentBoard).isEmpty() || !GameRules.getCurrentComponentMatches(componentBoard).isEmpty()) {
            for (int x = 0; x < componentBoard.length; x++) {
                for (int y = 0; y < componentBoard[x].length; y++) {
                    Collections.shuffle(components);
                    Class<? extends Component> component = components.get(0);
                    Component c = component.getConstructor(int.class, int.class).newInstance(x, y);
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

    private MatchState getCurrentState() {
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
        System.out.println(j.prettyPrint(moves));
        System.out.println(j.prettyPrint(move));

        return found;
    }
}
