package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.animations.ReceiveDamageAnimation;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.util.CardFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DamageAllAbility extends Ability {

    StateAnalyzer<Integer> amount;
    CardFilter filter;

    public DamageAllAbility() {
        super(new ArrayList<>());
    }

    public DamageAllAbility(StateAnalyzer<Integer> amount) {
        this.amount = amount;
    }

    public DamageAllAbility(CardFilter filter, StateAnalyzer<Integer> amount) {
        this.amount = amount;
        this.filter = filter;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        UseAbilityMove abilityMove = (UseAbilityMove) move;
        List<Card> destroyed = new ArrayList<>();
        List<MoveAnimation> animation = new ArrayList<>();

        int index = state.activeEntities.get(abilityMove.getCard().getOwnerUID()).indexOf(abilityMove.getCard());
        state.activeEntities.get(abilityMove.getPlayerUID()).get(index).setProgress(0);

        for (Card card : state.getAllCards()) {
            if (filter == null || filter.filter(card, state, move)) {
                int damage = amount.analyze(state, move);
                animation.add(new ReceiveDamageAnimation(card, damage, card.getOwnerUID()));
                card.recieveDamage(damage);
                if (card.isDead()) {
                    destroyed.add(card);
                }
            }
        }

        MoveResult result = new MoveResult(move, MatchState.record(state), animation);
        results.add(result);

        if (destroyed.isEmpty() == false) {
            DestroyCardAbility destroyAbility = new DestroyCardAbility(destroyed);
            List<MoveResult> r = destroyAbility.doAbility(state, move);
            results.addAll(r);
        }

        List<Card> drained = new ArrayList<>();
        drained.add(((UseAbilityMove) (move)).getCard());
        results.get(0).getAnimations().add(0, new ConsumeProgressAnimation(move.getPlayerUID(), drained));

        return results;
    }

}
