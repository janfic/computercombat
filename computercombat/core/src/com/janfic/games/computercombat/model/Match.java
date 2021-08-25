package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.components.*;
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

    public Match(Profile player1, Profile player2) {

        Map<Class<? extends Component>, Integer> componentFrequencies = new HashMap<>();
        componentFrequencies.put(CPUComponent.class, 3);
        componentFrequencies.put(GPUComponent.class, 3);
        componentFrequencies.put(RAMComponent.class, 3);
        componentFrequencies.put(PowerComponent.class, 3);
        componentFrequencies.put(StorageComponent.class, 3);
        componentFrequencies.put(BugComponent.class, 1);

        this.player1 = player1;
        this.player2 = player2;
        Map<Player, List<Card>> activeEntities = new HashMap<>();
        Map<Player, Computer> computers = new HashMap<>();
        Map<Player, SoftwareDeck> decks = new HashMap<>();

        activeEntities.put(player1.getActivePlayer(), new ArrayList<>());
        activeEntities.put(player2.getActivePlayer(), new ArrayList<>());
//        computers.put(player1.getActivePlayer(), new ArrayList<>());
//        computers.put(player2.getActivePlayer(), new ArrayList<>());
//        decks.put(player1.getActivePlayer(), new ArrayList<>());
//        decks.put(player2.getActivePlayer(), new ArrayList<>());

        try {
            this.currentState = new MatchState(player1.getActivePlayer(), player2.getActivePlayer(), makeBoard(componentFrequencies), activeEntities, computers, decks);
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

        while (!areAvailableComponentMatches(componentBoard) || !areCurrentComponentMatches(componentBoard)) {
            System.out.println("here");
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

    private boolean areCurrentComponentMatches(MatchState state) {
        Component[][] components = state.getComponentBoard();
        return areCurrentComponentMatches(components);
    }

    private boolean areCurrentComponentMatches(Component[][] components) {
        for (int x = 0; x < components.length - 2; x++) {
            for (int y = 0; y < components[x].length - 2; y++) {
                Class c = components[x][y].getClass();
                if (c == components[x + 1][y].getClass() && c == components[x + 2][y].getClass()) {
                    return true;
                }
                if (c == components[x][y + 1].getClass() && c == components[x][y + 2].getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areAvailableComponentMatches(MatchState state) {
        Component[][] components = state.getComponentBoard();
        return areAvailableComponentMatches(components);
    }

    private boolean areAvailableComponentMatches(Component[][] components) {
        for (int x = 0; x < components.length - 1; x++) {
            for (int y = 0; y < components[x].length - 1; y++) {
                //Horrizontal
                if (components[x][y].getClass() == components[x + 1][y].getClass()) {
                    //Top Left
                    if (x > 0 && y > 0) {
                        if (components[x][y].getClass() == components[x - 1][y - 1].getClass()) {
                            return true;
                        }
                    }
                    //Top Right
                    if (x < components.length - 2 && y > 0) {
                        if (components[x][y].getClass() == components[x + 2][y - 1].getClass()) {
                            return true;
                        }
                    }
                    //Bottom Left
                    if (x > 0 && y < components[x].length - 1) {
                        if (components[x][y].getClass() == components[x - 1][y + 1].getClass()) {
                            return true;
                        }
                    }
                    //Bottom Right
                    if (x < components.length - 2 && y < components[x].length - 1) {
                        if (components[x][y].getClass() == components[x + 2][y + 1].getClass()) {
                            return true;
                        }
                    }
                }

                //Verticle
                if (components[x][y].getClass() == components[x][y + 1].getClass()) {
                    //Top Left
                    if (x > 0 && y > 0) {
                        if (components[x][y].getClass() == components[x - 1][y - 1].getClass()) {
                            return true;
                        }
                    }
                    //Top Right
                    if (x < components.length - 1 && y > 0) {
                        if (components[x][y].getClass() == components[x + 1][y - 1].getClass()) {
                            return true;
                        }
                    }
                    //Bottom Left
                    if (x > 0 && y < components[x].length - 2) {
                        if (components[x][y].getClass() == components[x - 1][y + 2].getClass()) {
                            return true;
                        }
                    }
                    //Bottom Right
                    if (x < components.length - 1 && y < components[x].length - 2) {
                        if (components[x][y].getClass() == components[x + 1][y + 2].getClass()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private MatchState shuffleBoard(MatchState state) {
        MatchState nextState = new MatchState(state);
        Component[][] components = nextState.getComponentBoard();

        while (!areAvailableComponentMatches(nextState)) {

            List<Component> componentList = new ArrayList<>();
            for (Component[] component : components) {
                for (Component c : component) {
                    componentList.add(c);
                }
            }

            Collections.shuffle(componentList);

            int index = 0;
            for (int x = 0; x < components.length; x++) {
                for (int y = 0; y < components[x].length; y++) {
                    components[x][y] = componentList.get(index);
                    index++;
                }
            }
        }
        return nextState;
    }

    public MatchState getPlayerMatchState(Player player) {
        MatchState copy = new MatchState(currentState);
        return copy;
    }
}
