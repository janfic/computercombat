package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class MultiAbility extends Ability {

    List<Ability> abilities;

    public MultiAbility() {
        super(new ArrayList<>());
    }

    public MultiAbility(List<Filter> selectFilters, List<Ability> abilities) {
        super(selectFilters);
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MatchState lastState = new MatchState(state);
        for (Ability ability : abilities) {
            List<MoveResult> abilityResults = ability.doAbility(lastState, move);
            results.addAll(abilityResults);
            lastState = results.get(results.size() - 1).getNewState();
        }

        return results;
    }
}
