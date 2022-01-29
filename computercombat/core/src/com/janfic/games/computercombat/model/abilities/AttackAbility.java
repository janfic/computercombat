package com.janfic.games.computercombat.model.abilities;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.animations.AttackAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

public class AttackAbility extends Ability {

    ObjectMap<Card, List<Card>> attacks;

    public AttackAbility() {
        super(new ArrayList<>());
        this.attacks = null;
    }

    public AttackAbility(List<Filter> selectFilters, ObjectMap<Card, List<Card>> attacks) {
        super(selectFilters);
        this.attacks = attacks;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();
        MatchState newState = new MatchState(state);
        String currentUID = move.getPlayerUID();
        String opponentUID = newState.getOtherProfile(newState.currentPlayerMove).getUID();

        List<Card> destroyed = new ArrayList<>();
        List<MoveAnimation> animation = new ArrayList<>();
        for (Entry<Card, List<Card>> entry : attacks.entries()) {
            List<Card> attacked = entry.value;
            for (Card c : attacked) {
                if (c instanceof Software) {
                    for (Card cardAttacked : newState.activeEntities.get(opponentUID)) {
                        if (c.equals(cardAttacked)) {
                            cardAttacked.recieveDamage(entry.key.getAttack());
                            animation.add(new AttackAnimation(currentUID, opponentUID, attacks));
                            if (cardAttacked.isDead()) {
                                destroyed.add(cardAttacked);
                            }
                            break;
                        }
                    }
                } else if (c instanceof Computer) {
                    Computer cardAttacked = newState.computers.get(opponentUID);
                    cardAttacked.recieveDamage(entry.key.getAttack());
                    animation.add(new AttackAnimation(currentUID, opponentUID, attacks));
                }
            }
        }

        MoveResult result = new MoveResult(move, state, newState, animation);
        results.add(result);
        if (!destroyed.isEmpty()) {
            DestroyCardAbility destroyAbility = new DestroyCardAbility(destroyed);
            List<MoveResult> r = destroyAbility.doAbility(result.getNewState(), move);
            results.addAll(r);
        }
        return results;
    }
}
