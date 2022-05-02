package com.janfic.games.computercombat.model.players;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.HeuristicAnalyzer;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.IncreaseComponentTypeHeuristicAnalyzer;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.KeepComponentTypeHeuristicAnalyzer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public class MarkovChainBotPlayer extends Player {

    MatchState currentState;

    static Map<String, Map<String, Float>> markovChain = new HashMap<>();;

    public MarkovChainBotPlayer() {
    }

    public MarkovChainBotPlayer(String uid, Deck deck) {
        super(uid, deck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        currentState = state;
    }

    @Override
    public Move getMove() {
        List<Move> moves = GameRules.getAvailableMoves(currentState);
        int i = (int) (Math.random() * (moves.size() - 1));
        return moves.get(i);
    }

    @Override
    public void updateState(List<MoveResult> state) {
        try {
            MatchState s = (MatchState) state.get(state.size() - 1).getState().clone();
            if (state.get(0).getMove().getPlayerUID().equals(this.getUID())) {
                Map<String, Float> f = markovChain.getOrDefault(currentState.boardAsString(), new HashMap<>());
                HeuristicAnalyzer ha = new KeepComponentTypeHeuristicAnalyzer(1);
                if (f.size() >= 0) {
                    markovChain.put(currentState.boardAsString(), f);
                }
                f.put(s.boardAsString(), f.getOrDefault(s.boardAsString(), 0f) + ha.analyze(state));
            }
            this.currentState = s;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameOver(MatchResults results) {
        Json j = new Json();
        System.out.println(markovChain.size());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MarkovChainBotPlayer player = new MarkovChainBotPlayer();
        player.setUID(this.getUID());
        player.setDeck((Deck) this.getActiveDeck().clone());
        return player;
    }

}
