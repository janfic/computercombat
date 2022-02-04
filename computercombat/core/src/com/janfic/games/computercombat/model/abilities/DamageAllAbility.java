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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DamageAllAbility extends Ability {

    StateAnalyzer<Integer> amount;

    public DamageAllAbility() {
        super(new ArrayList<>());
    }

    public DamageAllAbility(StateAnalyzer<Integer> amount) {
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        UseAbilityMove abilityMove = (UseAbilityMove) move;
        MatchState newState = new MatchState(state);
        List<Card> destroyed = new ArrayList<>();
        List<MoveAnimation> animation = new ArrayList<>();

        int index = newState.activeEntities.get(abilityMove.getCard().getOwnerUID()).indexOf(abilityMove.getCard());
        newState.activeEntities.get(abilityMove.getPlayerUID()).get(index).setProgress(0);

        String playerUID = state.getOtherProfile(move.getPlayerUID()).getUID();

        for (Card card : newState.activeEntities.get(playerUID)) {
            int damage = amount.analyze(state, move);
            for (Card oldCard : state.activeEntities.get(playerUID)) {
                if (oldCard.equals(card)) {
                    animation.add(new ReceiveDamageAnimation(oldCard, damage, playerUID));
                    break;
                }
            }
            card.recieveDamage(damage);
            if (card.isDead()) {
                destroyed.add(card);
            }
        }

        MoveResult result = new MoveResult(move, state, newState, animation);
        results.add(result);

        if (destroyed.isEmpty() == false) {
            DestroyCardAbility destroyAbility = new DestroyCardAbility(destroyed);
            List<MoveResult> r = destroyAbility.doAbility(result.getNewState(), move);
            results.addAll(r);
        }

        List<Card> drained = new ArrayList<>();
        drained.add(((UseAbilityMove) (move)).getCard());
        results.get(0).getAnimations().add(0, new ConsumeProgressAnimation(move.getPlayerUID(), drained));

        return results;
    }

}
