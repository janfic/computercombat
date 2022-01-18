package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.Move;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.model.components.BugComponent;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.components.NetworkComponent;
import com.janfic.games.computercombat.model.components.PowerComponent;
import com.janfic.games.computercombat.model.components.RAMComponent;
import com.janfic.games.computercombat.model.components.StorageComponent;
import com.janfic.games.computercombat.model.moves.MatchComponentsMove;
import java.util.*;

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
        String uid = state.currentPlayerMove.getUID();
        for (Card card : state.activeEntities.get(uid)) {
            if (card.getRunProgress() >= card.getRunRequirements()) {
                UseAbilityMove m = new UseAbilityMove(uid, card, null, null);
                moves.add(m);
            }
        }
        Computer c = state.computers.get(uid);
        if (c.getRunProgress() >= c.getRunRequirements()) {
            UseAbilityMove m = new UseAbilityMove(uid, c, null, null);
            moves.add(m);
        }
        return moves;
    }

    @Deprecated
    public static boolean areCurrentComponentMatches(MatchState state) {
        Component[][] components = state.getComponentBoard();
        return areCurrentComponentMatches(components);
    }

    @Deprecated
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

    private static final int[][] possibleMatchesCoords = {
        {-1, -1, 0, 1, -1, -1, 0, -1},
        {1, -1, 0, 1, 1, -1, 0, -1},
        {0, -2, 0, 1, 0, -2, 0, -1},
        {0, -1, 0, 2, 0, 2, 0, 1},
        {-1, 1, 0, -1, -1, 1, 0, 1},
        {1, 1, 0, -1, 1, 1, 0, 1},
        {-1, -1, 1, 0, -1, -1, -1, 0},
        {-1, 1, 1, 0, -1, 1, -1, 0},
        {-2, 0, 1, 0, -2, 0, -1, 0},
        {-1, 0, 1, -1, 1, -1, 1, 0},
        {-1, 0, 1, 1, 1, 1, 1, 0},
        {-1, 0, 2, 0, 2, 0, 1, 0},
        {-1, -1, 1, -1, 0, 0, 0, -1},
        {-1, 1, 1, 1, 0, 0, 0, 1},
        {1, -1, 1, 1, 0, 0, 1, 0},
        {-1, -1, -1, 1, 0, 0, -1, 0}
    };

    public static Map<Integer, List<Component>> collectComponents(Component[] collected, Component[][] board) {
        int[][] marks = new int[8][8];
        int currentMark = 1;
        Map<Integer, List<Component>> r = new HashMap<>();
        for (int i = 0; i < collected.length; i++) {
            List<Component> list = new ArrayList<>();
            list.add(collected[i]);
            r.put(currentMark, list);
            currentMark++;
        }
        return r;
    }

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
            if (c.size() > 2) {
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
        for (int x = 0; x < components.length; x++) {
            for (int y = 0; y < components[x].length; y++) {
                for (int[] possibleMatch : possibleMatchesCoords) {
                    if (x + possibleMatch[0] < 0 || x + possibleMatch[0] > 7 || y + possibleMatch[1] < 0 || y + possibleMatch[1] > 7) {
                        continue;
                    }
                    if (x + possibleMatch[2] < 0 || x + possibleMatch[2] > 7 || y + possibleMatch[3] < 0 || y + possibleMatch[3] > 7) {
                        continue;
                    }
                    int x2 = x + possibleMatch[0], y2 = y + possibleMatch[1];
                    int x3 = x + possibleMatch[2], y3 = y + possibleMatch[3];
                    if (components[x][y].getClass().equals(components[x2][y2].getClass()) && components[x][y].getClass().equals(components[x3][y3].getClass())) {
                        possibleMatches.add(new Integer[]{x + possibleMatch[4], y + possibleMatch[5], x + possibleMatch[6], y + possibleMatch[7]});
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

    public static List<MoveResult> makeMove(MatchState originalState, Move move) {
        Json json = new Json();
        List<MoveResult> results = move.doMove(originalState);
        return results;
    }

    public static List<Component> getNewComponents(int count) {
        List<Component> components = new ArrayList<>();
        List<Class<? extends Component>> componentTypes = new ArrayList<>();
        for (Class<? extends Component> type : componentFrequencies.keySet()) {
            int frequency = componentFrequencies.get(type);
            componentTypes.addAll(Collections.nCopies(frequency, type));
        }
        for (int i = 0; i < count; i++) {
            int j = (int) (Math.random() * componentTypes.size());
            try {
                Component newComponent = componentTypes.get(j).getConstructor(int.class, int.class).newInstance(0, 0);
                components.add(newComponent);
            } catch (Exception e) {
            }
        }
        return components;
    }

    public static boolean isGameOver(MatchState state) {
        for (String string : state.computers.keySet()) {
            if (state.computers.get(string).isDead()) {
                state.isGameOver = true;
                for (Profile player : state.players) {
                    if (player.getUID().equals(string) == false) {
                        state.winner = player;
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
