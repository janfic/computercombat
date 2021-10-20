package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.MatchState;
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

        for (Card card : attacks.keySet()) {
            List<Card> attacked = attacks.get(card);
            for (Card c : attacked) {

            }
        }

        List<MoveAnimation> animation = new ArrayList<>();
        MoveResult result = new MoveResult(move, state, newState, animation);

        results.add(result);
        return results;
    }
}
