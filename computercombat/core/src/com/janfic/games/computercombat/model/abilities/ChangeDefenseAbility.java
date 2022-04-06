package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.CardFilter;
import com.janfic.games.computercombat.util.Filter;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class ChangeDefenseAbility extends Ability {

    CardFilter cardFilter;
    StateAnalyzer<Integer> amount;

    public ChangeDefenseAbility(List<Filter> selectFilters, CardFilter cardFilter, StateAnalyzer<Integer> amount) {
        super(selectFilters);
        this.cardFilter = cardFilter;
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        //STUBBED
        return null;
    }
}
