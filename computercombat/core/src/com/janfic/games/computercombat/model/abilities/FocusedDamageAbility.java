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
        MatchState newState = new MatchState(state);
        List<Card> destroyed = new ArrayList<>();
        List<MoveAnimation> animation = new ArrayList<>();

        int index = newState.activeEntities.get(abilityMove.getCard().getOwnerUID()).indexOf(abilityMove.getCard());
        newState.activeEntities.get(abilityMove.getPlayerUID()).get(index).setProgress(0);

        for (Card selectedSoftware : abilityMove.getSelectedSoftwares()) {
            for (String playerUID : newState.activeEntities.keySet()) {
                for (Card card : newState.activeEntities.get(playerUID)) {
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

        MoveResult result = new MoveResult(move, state, newState, animation);
        moveResults.add(result);

        if (destroyed.isEmpty() == false) {
            DestroyCardAbility destroyAbility = new DestroyCardAbility(destroyed);
            List<MoveResult> r = destroyAbility.doAbility(result.getNewState(), move);
            moveResults.addAll(r);
        }

        List<Card> drained = new ArrayList<>();
        drained.add(((UseAbilityMove) (move)).getCard());
        moveResults.get(0).getAnimations().add(0, new ConsumeProgressAnimation(move.getPlayerUID(), drained));

        moveResults.get(moveResults.size() - 1).getNewState().currentPlayerMove = newState.getOtherProfile(newState.currentPlayerMove);
        return moveResults;
    }

}
