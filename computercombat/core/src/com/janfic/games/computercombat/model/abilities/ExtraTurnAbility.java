package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class ExtraTurnAbility extends Ability {

    public ExtraTurnAbility() {
        super(new ArrayList<>());
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        state.currentPlayerMove = state.players.get(0).getUID().equals(move.getPlayerUID()) ? state.players.get(0).getUID() : state.players.get(1).getUID();

        MoveResult result = new MoveResult(move, MatchState.record(state), new ArrayList<>());

        results.add(result);
        return results;
    }

}
