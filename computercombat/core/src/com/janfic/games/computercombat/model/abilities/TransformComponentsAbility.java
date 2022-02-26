package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.animations.SpawnAnimation;
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

        Component[][] board = state.getComponentBoard();

        List<MoveAnimation> animations = new ArrayList<>();

        MoveAnimation consumeProgress = Ability.consumeCardProgress(state, move);

        List<Component> originalComponents = new ArrayList<>(), newComponents = new ArrayList<>();
        for (Component[] components : board) {
            for (Component component : components) {
                if (filter.filter(component, state, move)) {
                    int randomIndex = (int) (Math.random() * transformTypes.size());
                    Component c = new Component(transformTypes.get(randomIndex), component.getX(), component.getY());
                    originalComponents.add(new Component(component));
                    newComponents.add(c);
                    board[component.getX()][component.getY()].changeColor(transformTypes.get(randomIndex));
                    board[component.getX()][component.getY()].invalidate();
                    board[component.getX()][component.getY()].invalidateNeighbors();
                }
            }
        }

        animations.add(consumeProgress);
        animations.add(new SpawnAnimation(originalComponents, newComponents));

        MoveResult result = new MoveResult(move, MatchState.record(state), animations);
        List<MoveResult> afterMove = state.results(move);

        results.add(result);
        results.addAll(afterMove);

        return results;
    }

}
