package com.janfic.games.computercombat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class GameRules {

    public static List<Move> getAvailableMoves(MatchState state) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

    public static boolean areCurrentComponentMatches(MatchState state) {
        Component[][] components = state.getComponentBoard();
        return areCurrentComponentMatches(components);
    }

    public static boolean areCurrentComponentMatches(Component[][] components) {
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

    public static List<Integer[]> areAvailableComponentMatches(MatchState state) {
        Component[][] components = state.getComponentBoard();
        return areAvailableComponentMatches(components);
    }

    public static List<Integer[]> areAvailableComponentMatches(Component[][] components) {
        List<Integer[]> possibleMatches = new ArrayList<>();
        for (int x = 0; x < components.length - 1; x++) {
            for (int y = 0; y < components[x].length - 1; y++) {
                //Horrizontal
                if (components[x][y].getClass() == components[x + 1][y].getClass()) {
                    //Top Left
                    if (x > 0 && y > 0) {
                        if (components[x][y].getClass() == components[x - 1][y - 1].getClass()) {
                            possibleMatches.add(new Integer[]{x - 1, y - 1, x - 1, y});
                        }
                    }
                    //Top Right
                    if (x < components.length - 2 && y > 0) {
                        if (components[x][y].getClass() == components[x + 2][y - 1].getClass()) {
                            possibleMatches.add(new Integer[]{x + 2, y - 1, x + 2, y});
                        }
                    }
                    //Bottom Left
                    if (x > 0 && y < components[x].length - 1) {
                        if (components[x][y].getClass() == components[x - 1][y + 1].getClass()) {
                            possibleMatches.add(new Integer[]{x - 1, y + 1, x - 1, y});
                        }
                    }
                    //Bottom Right
                    if (x < components.length - 2 && y < components[x].length - 1) {
                        if (components[x][y].getClass() == components[x + 2][y + 1].getClass()) {
                            possibleMatches.add(new Integer[]{x + 2, y + 1, x + 2, y});
                        }
                    }
                }

                //Verticle
                if (components[x][y].getClass() == components[x][y + 1].getClass()) {
                    //Top Left
                    if (x > 0 && y > 0) {
                        if (components[x][y].getClass() == components[x - 1][y - 1].getClass()) {
                            possibleMatches.add(new Integer[]{x - 1, y - 1, x, y - 1});
                        }
                    }
                    //Top Right
                    if (x < components.length - 1 && y > 0) {
                        if (components[x][y].getClass() == components[x + 1][y - 1].getClass()) {
                            possibleMatches.add(new Integer[]{x + 1, y - 1, x, y - 1});
                        }
                    }
                    //Bottom Left
                    if (x > 0 && y < components[x].length - 2) {
                        if (components[x][y].getClass() == components[x - 1][y + 2].getClass()) {
                            possibleMatches.add(new Integer[]{x - 1, y + 2, x, y + 2});
                        }
                    }
                    //Bottom Right
                    if (x < components.length - 1 && y < components[x].length - 2) {
                        if (components[x][y].getClass() == components[x + 1][y + 2].getClass()) {
                            possibleMatches.add(new Integer[]{x + 1, y + 2, x, y + 2});
                        }
                    }
                }
            }
        }
        return possibleMatches;
    }

    public static MatchState shuffleBoard(MatchState state) {
        MatchState nextState = new MatchState(state);
        Component[][] components = nextState.getComponentBoard();

        while (areAvailableComponentMatches(nextState).isEmpty() == false) {

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
}
