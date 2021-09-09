package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Move.MatchComponentsMove;
import com.janfic.games.computercombat.model.components.BugComponent;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.components.NetworkComponent;
import com.janfic.games.computercombat.model.components.PowerComponent;
import com.janfic.games.computercombat.model.components.RAMComponent;
import com.janfic.games.computercombat.model.components.StorageComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author Jan Fic
 */
public class GameRules {

    public static final Map<Class<? extends Component>, Integer> componentFrequencies;

    static {
        componentFrequencies = new HashMap<>();
        componentFrequencies.put(CPUComponent.class, 3);
        //componentFrequencies.put(GPUComponent.class, 3);
        componentFrequencies.put(RAMComponent.class, 3);
        componentFrequencies.put(NetworkComponent.class, 3);
        componentFrequencies.put(PowerComponent.class, 3);
        componentFrequencies.put(StorageComponent.class, 3);
        componentFrequencies.put(BugComponent.class, 1);
    }

    public static List<Move> getAvailableMoves(MatchState state) {
        List<Move> moves = new ArrayList<>();
        List<Integer[]> matches = areAvailableComponentMatches(state);
        for (Integer[] match : matches) {
            Component a = state.getComponentBoard()[match[0]][match[1]];
            Component b = state.getComponentBoard()[match[2]][match[3]];
            MatchComponentsMove m = new MatchComponentsMove(state.currentPlayerMove.getUID(), a, b);
            moves.add(m);
        }
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

    private static final int[][] matches = {
        {0, 1, 0, 2},
        {0, -1, 0, -2},
        {1, 0, 2, 0},
        {-1, 0, -2, 0},
        {0, -1, 0, 1},
        {1, 0, -1, 0}
    };

    public static Map<Integer, List<Component>> getCurrentComponentMatches(Component[][] components) {
        int[][] marks = new int[8][8];
        int currentMark = 1;
        Map<Integer, List<Component>> r = new HashMap<>();
        for (int x = 0; x < components.length; x++) {
            for (int y = 0; y < components[x].length; y++) {
                if (marks[x][y] != 0) {
                    continue;
                }
                Stack<Component> stack = new Stack<>();
                stack.add(components[x][y]);

                while (stack.isEmpty() == false) {
                    Component currentComponent = stack.pop();
                    Class c = currentComponent.getClass();
                    int cx = currentComponent.getX();
                    int cy = currentComponent.getY();
                    for (int[] m : matches) {
                        if ((cx + m[0] >= 0 && cx + m[0] < 8 && cy + m[1] >= 0 && cy + m[1] < 8) == false
                                || (cx + m[2] >= 0 && cx + m[2] < 8 && cy + m[3] >= 0 && cy + m[3] < 8) == false) {
                            continue;
                        }
                        if (c == components[cx + m[0]][cy + m[1]].getClass() && c == components[cx + m[2]][cy + m[3]].getClass()) {
                            marks[cx][cy] = currentMark;
                            if (marks[cx + m[0]][cy + m[1]] == 0) {
                                stack.add(components[cx + m[0]][cy + m[1]]);
                            }
                            if (marks[cx + m[2]][cy + m[3]] == 0) {
                                stack.add(components[cx + m[2]][cy + m[3]]);
                            }
                            marks[cx + m[0]][cy + m[1]] = currentMark;
                            marks[cx + m[2]][cy + m[3]] = currentMark;
                        }
                    }
                }
                currentMark++;
            }
        }
        for (int i = 1; i <= currentMark; i++) {
            List<Component> c = new ArrayList<>();
            for (int x = 0; x < marks.length; x++) {
                for (int y = 0; y < marks[x].length; y++) {
                    if (marks[x][y] == i) {
                        c.add(components[x][y]);
                    }
                }
            }
            if (c.size() > 1) {
                r.put(i, c);
            }
        }
        return r;

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

    public static List<MoveResult> makeMove(MatchState state, Move move) {
        Json json = new Json();
        List<MoveResult> results = new ArrayList<>();
        List<Move> validMoves = getAvailableMoves(state);
        if (!validMoves.contains(move)) {
            return results;
        }
        if (move instanceof MatchComponentsMove) {
            System.out.println("IN IF");
            MatchComponentsMove matchMove = (MatchComponentsMove) move;
            MatchState currentState = matchMove.doMove(state);

            MatchState oldState = currentState, newState;

            System.out.println(areCurrentComponentMatches(oldState));
            System.out.println(json.prettyPrint(getCurrentComponentMatches(oldState.getComponentBoard())));
            while (areCurrentComponentMatches(oldState)) {
                Map<Integer, List<Component>> collect = getCurrentComponentMatches(oldState.getComponentBoard());
                List<Component> newComponents = new ArrayList<>();
                List<Integer> marks = new ArrayList<>(collect.keySet());
                List<Class<? extends Component>> componentTypes = new ArrayList<>();
                for (Class<? extends Component> type : componentFrequencies.keySet()) {
                    int frequency = componentFrequencies.get(type);
                    componentTypes.addAll(Collections.nCopies(frequency, type));
                }
                newState = new MatchState(oldState);

                for (Integer mark : marks) {
                    List<Component> components = collect.get(mark);
                    for (Component component : components) {
                        try {
                            int i = (int) (Math.random() * componentTypes.size());
                            Component newComponent = componentTypes.get(i).getConstructor(int.class, int.class).newInstance(component.getX(), component.getY());
                            newComponents.add(newComponent);
                            newState.componentBoard[component.getX()][component.getY()] = newComponent;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                MoveResult m = new MoveResult(move, oldState, newState, collect, newComponents);
                oldState = newState;
                results.add(m);
            }
        }
        System.out.println(json.prettyPrint(results));
        return results;
    }

    public static class MoveResult {

        private final Map<Integer, List<Component>> collectedComponents;
        private final List<Component> newComponents;
        private final MatchState oldState, newState;
        private final Move move;

        public MoveResult(Move move, MatchState oldState, MatchState newState, Map<Integer, List<Component>> collectedComponents, List<Component> newComponents) {
            this.collectedComponents = collectedComponents;
            this.newComponents = newComponents;
            this.move = move;
            this.newState = newState;
            this.oldState = oldState;
        }

        public Map<Integer, List<Component>> getCollectedComponents() {
            return collectedComponents;
        }

        public List<Component> getNewComponents() {
            return newComponents;
        }

        public MatchState getNewState() {
            return newState;
        }

        public Move getMove() {
            return move;
        }

        public MatchState getOldState() {
            return oldState;
        }

        public MoveResult() {
            this.collectedComponents = null;
            this.newComponents = null;
            this.oldState = null;
            this.newState = null;
            this.move = null;
        }
    }
}
