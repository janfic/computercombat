package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.animations.TransformComponentsAnimation;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.ComponentFilter;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class TransformComponentsAbility extends Ability {

    /**
     * Filters that specify which components will be selected for
     * transformation.
     */
    ComponentFilter filter;

    /**
     * List of types of components that transformed components can equally
     * transform into randomly.
     *
     */
    List<Integer> transformTypes;

    public TransformComponentsAbility() {
        super(new ArrayList<>());
    }

    public TransformComponentsAbility(List<Filter> selectFilters, ComponentFilter componentFilters, List<Integer> transformTypes) {
        super(selectFilters);
        this.filter = componentFilters;
        this.transformTypes = transformTypes;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MatchState newState = new MatchState(state);
        Component[][] board = newState.getComponentBoard();

        List<MoveAnimation> animations = new ArrayList<>();

        MoveAnimation consumeProgress = Ability.consumeCardProgress(newState, move);

        List<Component> originalComponents = new ArrayList<>(), newComponents = new ArrayList<>();
        for (Component[] components : board) {
            for (Component component : components) {
                if (filter.filter(component, state, move)) {
                    int randomIndex = (int) (Math.random() * transformTypes.size());
                    try {
                        Component c = Component.numberToComponent
                                .get(transformTypes.get(randomIndex))
                                .getConstructor(int.class, int.class)
                                .newInstance(component.getX(), component.getY());
                        board[component.getX()][component.getY()] = c;

                        originalComponents.add(component);
                        newComponents.add(c);
                    } catch (Exception e) {

                    }
                }
            }
        }

        animations.add(consumeProgress);
        animations.add(new TransformComponentsAnimation(originalComponents, newComponents));

        MoveResult result = new MoveResult(move, newState, newState, animations);
        List<MoveResult> afterMove = Move.collectComponentsCheck(newState, move);

        results.add(result);
        results.addAll(afterMove);

        return results;
    }

}
