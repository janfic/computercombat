package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.*;
import com.janfic.games.computercombat.util.NullifyingJson;
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

    private final int MOVE_TRIES = 3;

    public HeuristicBotPlayer() {
    }

    public HeuristicBotPlayer(String uid, Deck deck, List<HeuristicAnalyzer> heuristicAnalyzerPriorityList) {
        this(uid, deck);
        this.priorityList = heuristicAnalyzerPriorityList;
    }

    public HeuristicBotPlayer(String uid, Deck deck) {
        super(uid, deck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        this.currentState = state;
    }

    @Override
    public Move getMove() {
        List<Move> moves = GameRules.getAvailableMoves(currentState);
        Collections.shuffle(moves);
        for (Move move : moves) {
            long startTime = System.currentTimeMillis();
            double moveSum = 0;
            // Repeat move and find average score
            for (int j = 0; j < MOVE_TRIES; j++) {
                List<MoveResult> results = new ArrayList<>();
                try {
                    results = GameRules.makeMove((MatchState) currentState.clone(), move);
                    double totalScore = 0;
                    for (int i = 0; i < priorityList.size(); i++) {
                        HeuristicAnalyzer analyzer = priorityList.get(i);
                        double baseScore = analyzer.analyze(results);
                        double priorityScalar = Math.pow(2, i);
                        double priorityScore = priorityScalar * baseScore;
                        totalScore += priorityScore;
                    }
                    moveSum += totalScore;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            double moveAverage = moveSum / MOVE_TRIES;
            move.setValue(moveAverage);
            long endTime = System.currentTimeMillis();
        }
        moves.sort(new MoveValueComparator());
        return moves.get(0);
    }

    @Override
    public void updateState(List<MoveResult> state) {
        this.currentState = new MatchState(state.get(state.size() - 1).getState());
    }

    @Override
    public void gameOver(MatchResults results) {
    }

    public class MoveValueComparator implements Comparator<Move> {

        @Override
        public int compare(Move a, Move b) {

            double difference = b.getValue() - a.getValue();
            if (difference > 0) {
                return 1;
            } else if (difference < 0) {
                return -1;
            }
            return 0;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new HeuristicBotPlayer(this.getUID(), (Deck) this.getActiveDeck().clone());
    }
}
