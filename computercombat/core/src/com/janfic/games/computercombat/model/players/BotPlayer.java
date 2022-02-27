package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class BotPlayer extends Player {

    MatchState currentState;
    Player opponent;

    public BotPlayer() {
    }

    public BotPlayer(String uid, Deck deck) {
        super(uid, deck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        currentState = state;
        this.opponent = opponent;
    }

    @Override
    public Move getMove() {
        List<Move> moves = GameRules.getAvailableMoves(currentState);
        Collections.shuffle(moves);
        return moves.get(0);
    }

    @Override
    public void updateState(List<MoveResult> state) {
        this.currentState = state.get(state.size() - 1).getState();
    }

    @Override
    public void gameOver(MatchResults results) {
        // TODO
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new BotPlayer(this.getUID(), (Deck) this.getActiveDeck().clone());
    }
}
