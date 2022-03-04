package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.Move;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.model.moves.MatchComponentsMove;
import com.janfic.games.computercombat.util.CardFilter;
import com.janfic.games.computercombat.util.ComponentFilter;
import com.janfic.games.computercombat.util.Filter;
import java.util.*;

/**
 *
 * @author Jan Fic
 */
public class GameRules {

    public static final Map<Integer, Integer> componentFrequencies;

    static {
        componentFrequencies = new HashMap<>();
        componentFrequencies.put(1, 3);
        componentFrequencies.put(2, 3);
        componentFrequencies.put(3, 3);
        componentFrequencies.put(4, 3);
        componentFrequencies.put(6, 3);
        componentFrequencies.put(5, 1);
    }

    public static List<Move> getAvailableMoves(MatchState state) {
        List<Move> moves = new ArrayList<>();
        // Get MatchComponentsMoves
        List<Integer[]> matches = areAvailableComponentMatches(state);
        for (Integer[] match : matches) {
            Component a = state.getComponentBoard()[match[0]][match[1]];
            Component b = state.getComponentBoard()[match[2]][match[3]];
            MatchComponentsMove m = new MatchComponentsMove(state.currentPlayerMove.getUID(), a, b);
            moves.add(m);
        }
        // Get UseAbilityMoves
        String uid = state.currentPlayerMove.getUID();
        for (Card card : state.activeEntities.get(uid)) {
            if (card.getRunProgress() >= card.getRunRequirements()) {
                List<UseAbilityMove> generatedMoves = generateMovesWithSelection(0, card, state, new ArrayList<>(), new ArrayList<>());
                moves.addAll(generatedMoves);
            }
        }
        Card c = state.computers.get(uid);
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

    /**
     * Given a list of components that have been collected, generate the marks
     * for those components.
     *
     * @param collected
     * @param board
     * @return
     */
    public static Map<Integer, List<Component>> collectComponents(Component[] collected, Component[][] board) {
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

    /**
     * Finds existing matches that are on the board.
     *
     * @param components
     * @return
     */
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
                    Integer color = currentComponent.getColor();
                    int cx = currentComponent.getX();
                    int cy = currentComponent.getY();
                    for (int[] m : matches) {
                        if ((cx + m[0] >= 0 && cx + m[0] < 8 && cy + m[1] >= 0 && cy + m[1] < 8) == false
                                || (cx + m[2] >= 0 && cx + m[2] < 8 && cy + m[3] >= 0 && cy + m[3] < 8) == false) {
                            continue;
                        }
                        if (color == components[cx + m[0]][cy + m[1]].getColor() && color == components[cx + m[2]][cy + m[3]].getColor()) {
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
                    if (components[x][y].getColor() == components[x2][y2].getColor() && components[x][y].getColor() == components[x3][y3].getColor()) {
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
        List<MoveResult> results;
        if (move instanceof UseAbilityMove) {
            UseAbilityMove m = (UseAbilityMove) move;
            m.getCard().setAbility(Ability.getAbilityFromCode(m.getCard().getAbility()));
            results = m.doMove(originalState);
        } else {
            results = move.doMove(originalState);
        }
        return results;
    }

    public static int getNewColor() {
        return getNewComponents(1).get(0).getColor();
    }

    public static List<Component> getNewComponents(int count) {
        List<Component> components = new ArrayList<>();
        List<Integer> componentTypes = new ArrayList<>();
        for (Integer type : componentFrequencies.keySet()) {
            int frequency = componentFrequencies.get(type);
            componentTypes.addAll(Collections.nCopies(frequency, type));
        }
        for (int i = 0; i < count; i++) {
            int j = (int) (Math.random() * componentTypes.size());
            try {
                Component newComponent = new Component(componentTypes.get(j), 0, 0);
                components.add(newComponent);
            } catch (Exception e) {
            }
        }
        return components;
    }

    public static List<Integer> getNewComponentsColors(int count) {
        List<Integer> components = new ArrayList<>();
        List<Integer> componentTypes = new ArrayList<>();
        for (Integer type : componentFrequencies.keySet()) {
            int frequency = componentFrequencies.get(type);
            componentTypes.addAll(Collections.nCopies(frequency, type));
        }
        for (int i = 0; i < count; i++) {
            int j = (int) (Math.random() * componentTypes.size());
            components.add(componentTypes.get(j));
        }
        return components;
    }

    public static boolean isGameOver(MatchState state) {
        for (String string : state.computers.keySet()) {
            boolean nomorecards = state.decks.get(string).getStack().size() == 0 && state.activeEntities.get(string).size() == 0;
            if (state.computers.get(string).isDead() || nomorecards) {
                state.isGameOver = true;
                state.winner = state.getOtherProfile(string);
                return true;
            }
        }
        return false;
    }

    private static List<UseAbilityMove> generateMovesWithSelection(int index, Card card, MatchState state, List<Component> selectedComponents, List<Card> selectedCards) {
        List<UseAbilityMove> moves = new ArrayList<>();

        String uid = state.currentPlayerMove.getUID();
        List<Filter> selectFilters = card.getAbility().getSelectFilters();

        if (index >= selectFilters.size()) {
            List<Component> selectedComponentsClone = new ArrayList<>(selectedComponents);
            List<Card> selectedCardsClone = new ArrayList<>(selectedCards);
            moves.add(new UseAbilityMove(uid, card, selectedComponentsClone, selectedCardsClone));
        } else {
            Filter filter = selectFilters.get(index);
            if (filter instanceof ComponentFilter) {
                List<Component> sComps = new ArrayList<>(selectedComponents);
                List<Card> sCards = new ArrayList<>(selectedCards);
                List<Component> selectableComponents = state.getComponentsByFilter(
                        (ComponentFilter) filter, new UseAbilityMove(uid, card, sComps, sCards)
                );
                for (Component selectableComponent : selectableComponents) {
                    sComps.add(selectableComponent);
                    moves.addAll(generateMovesWithSelection(index + 1, card, state, sComps, sCards));
                    sComps.remove(sComps.size() - 1);
                }
            } else if (filter instanceof CardFilter) {
                List<Component> sComps = new ArrayList<>(selectedComponents);
                List<Card> sCards = new ArrayList<>(selectedCards);
                List<Card> selectableCards = state.getCardsByFilter(
                        (CardFilter) filter, new UseAbilityMove(uid, card, sComps, sCards)
                );
                for (Card selectableCard : selectableCards) {
                    sCards.add(selectableCard);
                    moves.addAll(generateMovesWithSelection(index + 1, card, state, sComps, sCards));
                    sCards.remove(sCards.size() - 1);
                }
            }
        }
        return moves;
    }
}
