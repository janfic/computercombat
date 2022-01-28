package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.animations.SwitchAnimation;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import java.util.ArrayList;
import java.util.List;

/**
 * Switches Two Components. Support for any two components on the board is
 * implemented. Uses the Switch Animation
 *
 * @author Jan Fic
 */
public class SwitchComponentsAbility extends Ability {

    public SwitchComponentsAbility() {
        super(2, 0);
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MatchState newState = new MatchState(state);
        UseAbilityMove useAbility = (UseAbilityMove) move;

        Component[][] newBoard = newState.componentBoard;
        Component a = useAbility.getSelectedComponents().get(0);
        Component b = useAbility.getSelectedComponents().get(1);
        
        Component bb = newBoard[b.getX()][b.getY()];
        Component ba = newBoard[a.getX()][a.getY()];
        int oldBX = b.getX(), oldBY = b.getY();
        bb.setPosition(a.getX(), a.getY());
        ba.setPosition(oldBX, oldBY);
        newBoard[ba.getX()][ba.getY()] = ba;
        newBoard[bb.getX()][bb.getY()] = bb;

        List<Card> drained = new ArrayList<>();
        drained.add(((UseAbilityMove) (move)).getCard());

        List<MoveAnimation> anims = new ArrayList<>();
        anims.add(new ConsumeProgressAnimation(move.getPlayerUID(), drained));
        anims.add(new SwitchAnimation(bb, ba));

        MoveResult r = new MoveResult(move, state, newState, anims);
        List<MoveResult> collectCheckResults = Move.collectComponentsCheck(r.getNewState(), move);

        results.add(r);
        results.addAll(collectCheckResults);
        return results;
    }

}
