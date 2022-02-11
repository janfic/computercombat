package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.animations.DrawAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DrawAbility extends Ability {

    List<StateAnalyzer<Integer>> cards;

    public DrawAbility() {
        super(new ArrayList<>());
    }

    public DrawAbility(List<StateAnalyzer<Integer>> cards) {
        this.cards = cards;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> r = new ArrayList<>();

        UseAbilityMove abilityMove = (UseAbilityMove) move;
        MatchState newState = new MatchState(state);

        List<Card> cards = newState.activeEntities.get(move.getPlayerUID());
        Deck deck = newState.decks.get(move.getPlayerUID());

        List<Software> drawnCards = new ArrayList<>();
        if (this.cards == null) {
            if (deck.size() > 0) {
                Software s = deck.draw();
                s.setOwnerUID(move.getPlayerUID());
                cards.add(s);
                drawnCards.add(s);
            }
        } else {
            for (StateAnalyzer<Integer> card : this.cards) {
                Software s = SQLAPI.getSingleton().getCardById(card.analyze(state, move), move.getPlayerUID());
                s.setOwnerUID(move.getPlayerUID());
                s.generateMatchID();
                cards.add(s);
                drawnCards.add(s);
            }
        }

        List<MoveAnimation> animations = new ArrayList<>();
        if (abilityMove.getCard() instanceof Computer) {
            newState.computers.get(move.getPlayerUID()).setProgress(0);
        } else {
            for (Card card : newState.activeEntities.get(move.getPlayerUID())) {
                if (card.equals(abilityMove.getCard())) {
                    card.setProgress(0);
                }
            }

        }

        List<Card> drained = new ArrayList<>();
        drained.add(((UseAbilityMove) (move)).getCard());
        ConsumeProgressAnimation drainAnimation = new ConsumeProgressAnimation(move.getPlayerUID(), drained);

        DrawAnimation drawAnimation = new DrawAnimation(move.getPlayerUID(), drawnCards);

        for (Player player : newState.players) {
            if (player.getUID().equals(move.getPlayerUID())) {
                newState.currentPlayerMove = newState.getOtherProfile(player);
            }
        }

        animations.add(drainAnimation);
        animations.add(drawAnimation);

        MoveResult result = new MoveResult(move, state, newState, animations);

        r.add(result);
        return r;
    }
}
