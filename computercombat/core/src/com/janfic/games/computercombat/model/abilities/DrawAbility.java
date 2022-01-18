package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.animations.DrawAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DrawAbility extends Ability {

    public DrawAbility() {
        super(0, 0);
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> r = new ArrayList<>();

        MatchState newState = new MatchState(state);
        newState.computers.get(move.getPlayerUID()).setProgress(0);
        List<Card> cards = newState.activeEntities.get(move.getPlayerUID());
        Deck deck = newState.decks.get(move.getPlayerUID());

        List<Software> drawnCards = new ArrayList<>();
        if (deck.size() > 0) {
            Software s = deck.draw();
            cards.add(s);
            drawnCards.add(s);
        }

        List<Card> drained = new ArrayList<>();

        drained.add(((UseAbilityMove) (move)).getCard());
        ConsumeProgressAnimation drainAnimation = new ConsumeProgressAnimation(move.getPlayerUID(), drained);
        DrawAnimation drawAnimation = new DrawAnimation(move.getPlayerUID(), drawnCards);

        for (Profile player : newState.players) {
            if (player.getUID().equals(move.getPlayerUID())) {
                newState.currentPlayerMove = newState.getOtherProfile(player);
            }
        }

        List<MoveAnimation> animations = new ArrayList<>();
        animations.add(drainAnimation);
        animations.add(drawAnimation);

        MoveResult result = new MoveResult(move, state, newState, animations);

        r.add(result);
        return r;
    }
}
