package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.TransformCardAnimation;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.util.CardFilter;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class TransformCardAbility extends Ability {

    List<CardFilter> oldCards;
    List<Integer> newCards;

    public TransformCardAbility() {
        super(new ArrayList<>());
    }

    public TransformCardAbility(List<CardFilter> oldCards, List<Integer> newCards, List<Filter> selectFilters) {
        super(selectFilters);
        this.oldCards = oldCards;
        this.newCards = newCards;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        List<MoveAnimation> animations = new ArrayList<>();
        MoveAnimation consumeProgress = Ability.consumeCardProgress(state, move);

        List<Card> old = new ArrayList<>();
        List<Card> newC = new ArrayList<>();

        for (CardFilter filter : oldCards) {
            for (String uid : state.activeEntities.keySet()) {
                for (int i = 0; i < state.activeEntities.get(uid).size(); i++) {
                    Card oldCard = state.activeEntities.get(uid).get(i);
                    if (filter.filter(oldCard, state, move)) {
                        Card newCard = SQLAPI.getSingleton().getCardById(newCards.get(oldCards.indexOf(filter)), oldCard.getOwnerUID());
                        newCard.generateMatchID();
                        state.activeEntities.get(uid).set(i, newCard);
                        old.add(oldCard);
                        newC.add(newCard);
                    }
                }
            }
        }
        if (consumeProgress != null) {
            animations.add(consumeProgress);
        }
        animations.add(new TransformCardAnimation(old, newC));
        state.currentPlayerMove = state.getOtherProfile(state.currentPlayerMove);
        MoveResult result = new MoveResult(move, MatchState.record(state), animations);

        results.add(result);

        return results;
    }
}
