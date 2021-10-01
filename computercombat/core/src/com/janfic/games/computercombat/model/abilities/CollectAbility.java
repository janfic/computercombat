package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Jan Fic
 */
public class CollectAbility extends Ability {

    List<CollectFilter> filters;

    public CollectAbility(int selectComponents, int selectSoftwares, List<CollectFilter> filters) {
        super(selectComponents, selectSoftwares);
        this.filters = filters;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        Stream<Component> components = state.getComponentsAsList().stream();
        for (CollectFilter filter : filters) {
            components = components.filter((c) -> {
                return filter.filter(state, move, c);
            });
        }
        Component[] c = (Component[]) components.toArray();
        return null;
    }

    public interface CollectFilter {

        public boolean filter(MatchState state, Move move, Component c);
    }
}
