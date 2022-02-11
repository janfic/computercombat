package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class BotPlayer extends Player {

    public BotPlayer(String uid, Deck deck) {
        super(uid, deck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        // TODO
    }

    @Override
    public Move getMove() {
        // TODO
        return null;
    }

    @Override
    public void updateState(List<MoveResult> state) {
        // TODO
    }

    @Override
    public void gameOver(MatchResults results) {
        // TODO
    }
}
