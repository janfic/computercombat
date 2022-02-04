package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Jan Fic
 */
public class CollectAbility extends Ability {

    List<CollectFilter> filters;
    StateAnalyzer<Integer> amount;

    public CollectAbility() {
        super(new ArrayList<>());
    }

    public CollectAbility(List<Filter> selectFilters, List<CollectFilter> filters, StateAnalyzer<Integer> amount) {
        super(selectFilters);
        this.filters = filters;
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MatchState newState = new MatchState(state);
        UseAbilityMove useAbility = (UseAbilityMove) move;
        int index = newState.activeEntities.get(useAbility.getPlayerUID()).indexOf(useAbility.getCard());
        newState.activeEntities.get(useAbility.getPlayerUID()).get(index).setProgress(0);
        Stream<Component> components = newState.getComponentsAsList().stream();
        for (CollectFilter filter : filters) {
            components = components.filter((c) -> {
                return filter.filter(state, move, c);
            });
        }
        List<Component> list = components.collect(Collectors.toList());
        Collections.shuffle(list);
        int count = amount.analyze(state, move);
        list = list.subList(0, Math.min(count, list.size()));
        Component[] c = list.toArray(new Component[0]);
        Map<Integer, List<Component>> collected = GameRules.collectComponents(c, newState.getComponentBoard());
        List<MoveResult> result = Move.collectComponents(collected, state, newState, move);
        List<Card> drained = new ArrayList<>();
        drained.add(((UseAbilityMove) (move)).getCard());
        result.get(0).getAnimations().add(0, new ConsumeProgressAnimation(move.getPlayerUID(), drained));
        List<MoveResult> afterCollection = Move.collectComponentsCheck(result.get(result.size() - 1).getNewState(), move);

        results.addAll(result);
        results.addAll(afterCollection);
        return results;
    }

    public List<CollectFilter> getFilters() {
        return filters;
    }
}
