package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.animations.ReceiveDamageAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttackAbility extends Ability {

    Map<Card, List<Card>> attacks;

    public AttackAbility() {
        super(0, 0);
        this.attacks = null;
    }

    public AttackAbility(int selectedComponents, int selectedSoftware, Map<Card, List<Card>> attacks) {
        super(selectedComponents, selectedSoftware);
        this.attacks = attacks;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();
        MatchState newState = new MatchState(state);
        String currentUID = move.getPlayerUID();
        String opponentUID = newState.getOtherProfile(newState.currentPlayerMove).getUID();

        List<MoveAnimation> animation = new ArrayList<>();
        for (Card card : attacks.keySet()) {
            boolean isMovePlayers = newState.activeEntities.get(currentUID).contains(card);
            List<Card> attacked = attacks.get(card);
            for (Card c : attacked) {
                if (isMovePlayers) {
                    for (Card cardAttacked : newState.activeEntities.get(opponentUID)) {
                        if (c.equals(cardAttacked)) {
                            animation.add(new ReceiveDamageAnimation(cardAttacked, card.getAttack(), opponentUID));
                            cardAttacked.recieveDamage(card.getAttack());
                            break;
                        }
                    }
                }
            }
        }

        MoveResult result = new MoveResult(move, state, newState, animation);

        results.add(result);
        return results;
    }
}
