package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.CardToFrontAnimation;
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
public class CardToFrontAbility extends Ability {

    public CardToFrontAbility() {
    }

    public CardToFrontAbility(List<Filter> selectFilters) {
        super(selectFilters);
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        UseAbilityMove useAbilityMove = (UseAbilityMove) move;

        MatchState newState = new MatchState(state);
        MoveAnimation consume = Ability.consumeCardProgress(newState, move);

        List<Card> toFront = new ArrayList<>();
        toFront.addAll(useAbilityMove.getSelectedSoftwares());

        for (Card card : useAbilityMove.getSelectedSoftwares()) {
            int cardIndex = newState.activeEntities.get(card.getOwnerUID()).indexOf(card);
            Card newStateCard = newState.activeEntities.get(card.getOwnerUID()).get(cardIndex);
            newState.activeEntities.get(card.getOwnerUID()).remove(cardIndex);
            newState.activeEntities.get(card.getOwnerUID()).add(0, newStateCard);
        }

        newState.currentPlayerMove = newState.getOtherProfile(newState.currentPlayerMove);

        List<MoveAnimation> animations = new ArrayList<>();
        animations.add(consume);
        animations.add(new CardToFrontAnimation(toFront));

        MoveResult result = new MoveResult(move, state, newState, animations);
        results.add(result);

        return results;
    }

}
