package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.Profile;

/**
 *
 * @author Jan Fic
 */
public class BotPlayer extends Player {

    public BotPlayer(String uid, Profile profile, Computer computer) {
        super(uid, profile.getDefensiveDeck());
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
    public void updateState(MatchState state) {
        // TODO
    }
}
