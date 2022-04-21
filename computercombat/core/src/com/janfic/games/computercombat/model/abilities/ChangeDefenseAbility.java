package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.ChangeStatAnim;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.CardFilter;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class ChangeDefenseAbility extends Ability {

    CardFilter cardFilter;
    StateAnalyzer<Integer> amount;

    public ChangeDefenseAbility() {
        super(new ArrayList<>());
    }

    public ChangeDefenseAbility(List<Filter> selectFilters, CardFilter cardFilter, StateAnalyzer<Integer> amount) {
        super(selectFilters);
        this.cardFilter = cardFilter;
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();
        String currentUID = move.getPlayerUID();
        String opponentUID = state.getOtherProfile(state.currentPlayerMove);

        List<MoveAnimation> animation = new ArrayList<>();
        MoveAnimation consume = Ability.consumeCardProgress(state, move);
        animation.add(consume);

        for (Card card : state.getAllCards()) {
            if (cardFilter.filter(card, state, move)) {
                int amount = this.amount.analyze(state, move);
                card.changeArmor(amount);
                animation.add(new ChangeStatAnim("armor", amount, card, card.getOwnerUID()));
            }
        }

        state.currentPlayerMove = state.getOtherProfile(state.currentPlayerMove);
        MoveResult result = new MoveResult(move, MatchState.record(state), animation);
        results.add(result);
        return results;
    }
}
