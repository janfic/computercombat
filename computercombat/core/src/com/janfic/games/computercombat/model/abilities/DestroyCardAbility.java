package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.animations.DestroyCardAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DestroyCardAbility extends Ability {

    List<Card> destroyed;

    public DestroyCardAbility() {
    }

    public DestroyCardAbility(List<Card> destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        List<MoveAnimation> animations = new ArrayList<>();
        List<Card> removed = new ArrayList<>();
        for (Card card : destroyed) {
            for (Card c : state.activeEntities.get(card.getOwnerUID())) {
                if (c.equals(card)) {
                    removed.add(c);
                    List<Card> destroy = new ArrayList<>();
                    destroy.add(c);
                    animations.add(new DestroyCardAnimation(c.getOwnerUID(), destroy));
                }
            }
        }
        for (Card card : removed) {
            state.activeEntities.get(card.getOwnerUID()).remove(card);
        }
        MoveResult result = new MoveResult(move, MatchState.record(state), animations);

        results.add(result);
        return results;
    }
}
