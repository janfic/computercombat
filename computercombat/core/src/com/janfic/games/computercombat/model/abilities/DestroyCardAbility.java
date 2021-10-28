package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.MatchState;
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

    String playerUID;
    List<Card> destroyed;

    public DestroyCardAbility() {
    }

    public DestroyCardAbility(String playerUID, List<Card> destroyed) {
        this.playerUID = playerUID;
        this.destroyed = destroyed;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        List<MoveAnimation> animations = new ArrayList<>();
        MatchState newState = new MatchState(state);
        List<Card> removed = new ArrayList<>();
        for (Card card : destroyed) {
            for (Card c : newState.activeEntities.get(playerUID)) {
                if (c.equals(card)) {
                    removed.add(c);
                    List<Card> destroy = new ArrayList<>();
                    destroy.add(c);
                    animations.add(new DestroyCardAnimation(playerUID, destroy));
                }
            }
        }
        newState.activeEntities.get(playerUID).removeAll(removed);
        MoveResult result = new MoveResult(move, state, newState, animations);

        results.add(result);
        return results;
    }
}
