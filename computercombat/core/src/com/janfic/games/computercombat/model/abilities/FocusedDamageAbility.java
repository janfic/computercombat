package com.janfic.games.computercombat.model.abilities;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.animations.ReceiveDamageAnimation;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class FocusedDamageAbility extends Ability {

    StateAnalyzer<Integer> amount;

    public FocusedDamageAbility() {
        super(new ArrayList<>());
    }

    public FocusedDamageAbility(List<Filter> filter, StateAnalyzer<Integer> amount) {
        super(filter);
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> moveResults = new ArrayList<>();

        UseAbilityMove abilityMove = (UseAbilityMove) move;
        List<Card> destroyed = new ArrayList<>();
        List<MoveAnimation> animation = new ArrayList<>();
        MoveAnimation consume = Ability.consumeCardProgress(state, move);
        animation.add(consume);

        int index = state.activeEntities.get(abilityMove.getCard().getOwnerUID()).indexOf(abilityMove.getCard());
        state.activeEntities.get(abilityMove.getPlayerUID()).get(index).setProgress(0);

        for (Card selectedSoftware : abilityMove.getSelectedSoftwares()) {
            for (String playerUID : state.activeEntities.keySet()) {
                for (Card card : state.activeEntities.get(playerUID)) {
                    if (card.equals(selectedSoftware)) {
                        int damage = amount.analyze(state, move);
                        card.recieveDamage(damage);
                        animation.add(new ReceiveDamageAnimation(selectedSoftware, damage, card.getOwnerUID()));
                        if (card.isDead()) {
                            destroyed.add(card);
                        }
                    }
                }
            }
        }
        state.currentPlayerMove = state.getOtherProfile(state.currentPlayerMove);
        MoveResult result = new MoveResult(move, MatchState.record(state), animation);
        moveResults.add(result);

        if (destroyed.isEmpty() == false) {
            DestroyCardAbility destroyAbility = new DestroyCardAbility(destroyed);
            List<MoveResult> r = destroyAbility.doAbility(state, move);
            moveResults.addAll(r);
        }
        return moveResults;
    }

}
