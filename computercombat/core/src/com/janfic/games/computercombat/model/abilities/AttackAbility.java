package com.janfic.games.computercombat.model.abilities;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.animations.AttackAnimation;
import com.janfic.games.computercombat.model.animations.ReceiveDamageAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.ArrayList;
import java.util.List;

public class AttackAbility extends Ability {

    ObjectMap<Card, List<Card>> attacks;

    public AttackAbility() {
        super(0, 0);
        this.attacks = null;
    }

    public AttackAbility(int selectedComponents, int selectedSoftware, ObjectMap<Card, List<Card>> attacks) {
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
        for (Entry<Card, List<Card>> entry : attacks.entries()) {
            boolean isMovePlayers = newState.activeEntities.get(currentUID).contains(entry.key);
            List<Card> attacked = entry.value;
            for (Card c : attacked) {
                if (isMovePlayers) {
                    for (Card cardAttacked : newState.activeEntities.get(opponentUID)) {
                        if (c.equals(cardAttacked)) {
                            animation.add(new AttackAnimation(currentUID, opponentUID, attacks));
                            //animation.add(new ReceiveDamageAnimation(cardAttacked, entry.key.getAttack(), opponentUID));
                            cardAttacked.recieveDamage(entry.key.getAttack());
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
