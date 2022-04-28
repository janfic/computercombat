package com.janfic.games.computercombat.model.abilities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.animations.AttackAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttackAbility extends Ability {

    Map<String, Array<Integer>> attacks;

    public AttackAbility() {
        super(new ArrayList<>());
        this.attacks = null;
    }

    public AttackAbility(List<Filter> selectFilters, Map<String, Array<Integer>> attacks) {
        super(selectFilters);
        this.attacks = attacks;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();
        String currentUID = move.getPlayerUID();
        String opponentUID = state.getOtherProfile(state.currentPlayerMove);

        List<Card> destroyed = new ArrayList<>();
        List<MoveAnimation> animation = new ArrayList<>();
        for (String key : attacks.keySet()) {
            Array<Integer> attacked = attacks.get(key);
            for (Integer c : attacked) {
                Card att = state.getCardMyMatchID(Integer.parseInt(key));
                if (att.getID() > 0) {
                    for (Card cardAttacked : state.activeEntities.get(opponentUID)) {
                        if (c == cardAttacked.getMatchID()) {
                            cardAttacked.recieveDamage(state.getCardMyMatchID(Integer.parseInt(key)).getAttack());
                            animation.add(new AttackAnimation(currentUID, opponentUID, attacks));
                            if (cardAttacked.isDead()) {
                                destroyed.add(cardAttacked);
                            }
                            break;
                        }
                    }
                } else if (att.getID() == 0) {
                    Card cardAttacked = state.computers.get(opponentUID);
                    cardAttacked.recieveDamage(state.getCardMyMatchID(Integer.parseInt(key)).getAttack());
                    animation.add(new AttackAnimation(currentUID, opponentUID, attacks));
                }
            }
        }

        MoveResult result = new MoveResult(move, MatchState.record(state), animation);
        results.add(result);
        if (!destroyed.isEmpty()) {
            DestroyCardAbility destroyAbility = new DestroyCardAbility(destroyed);
            List<MoveResult> r = destroyAbility.doAbility(state, move);
            results.addAll(r);
        }
        return results;
    }
}
