package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.ChargedAbilitiesHeuristicAnalyzer;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.ComponentsCollectedHeuristicAnalyzer;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.DrewCardHeuristicAnalyzer;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.ExtraTurnHeuristicAnalyzer;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.HeuristicAnalyzer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author janfc
 */
public class HeuristicBotPlayer extends Player {

    MatchState currentState;

    List<HeuristicAnalyzer> priorityList;

    public HeuristicBotPlayer() {
    }

    public HeuristicBotPlayer(String uid, Deck deck) {
        super(uid, deck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        this.currentState = state;
        this.priorityList = new ArrayList<>();
        this.priorityList.add(new ChargedAbilitiesHeuristicAnalyzer());
        this.priorityList.add(new DrewCardHeuristicAnalyzer());
    }

    @Override
    public Move getMove() {
        List<Move> moves = GameRules.getAvailableMoves(currentState);
        Collections.shuffle(moves);

        for (Move move : moves) {
            List<MoveResult> results = GameRules.makeMove(currentState, move);
            double totalScore = 0;
            for (int i = 0; i < priorityList.size(); i++) {
                HeuristicAnalyzer analyzer = priorityList.get(i);
                double baseScore = analyzer.analyze(results);
                double priorityScalar = Math.pow(2, i);
                double priorityScore = priorityScalar * baseScore;
                totalScore += priorityScore;
            }
            move.setValue(totalScore);
        }

        moves.sort(new MoveValueComparator());

        return moves.get(0);
    }

    @Override
    public void updateState(List<MoveResult> state) {
        this.currentState = state.get(state.size() - 1).getNewState();
    }

    @Override
    public void gameOver(MatchResults results) {
    }

    public class MoveValueComparator implements Comparator<Move> {

        @Override
        public int compare(Move a, Move b) {
            return (int) Math.ceil(b.getValue() - a.getValue());
        }
    }
}
