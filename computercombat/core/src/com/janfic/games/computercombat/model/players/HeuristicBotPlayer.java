package com.janfic.games.computercombat.model.players;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.players.heuristicanalyzers.*;
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

    public HeuristicBotPlayer(String uid, Deck deck) {
        super(uid, deck);
    }

    @Override
    public void beginMatch(MatchState state, Player opponent) {
        this.currentState = state;
        this.priorityList = new ArrayList<>();
        this.priorityList.add(new ExtraTurnHeuristicAnalyzer());
        this.priorityList.add(new DrewCardHeuristicAnalyzer());
        this.priorityList.add(new UseAbilityHeuristicAnalyzer());
    }

    @Override
    public Move getMove() {
        List<Move> moves = GameRules.getAvailableMoves(currentState);
        Collections.shuffle(moves);
        Json json = new Json();
        for (Move move : moves) {
            long startTime = System.currentTimeMillis();
            double moveSum = 0;
            // Repeat move and find average score
            for (int j = 0; j < MOVE_TRIES; j++) {
                List<MoveResult> results = GameRules.makeMove(currentState, move);
                double totalScore = 0;
                for (int i = 0; i < priorityList.size(); i++) {
                    HeuristicAnalyzer analyzer = priorityList.get(i);
                    double baseScore = analyzer.analyze(results);
                    double priorityScalar = Math.pow(2, i);
                    double priorityScore = priorityScalar * baseScore;
                    totalScore += priorityScore;
                }
                moveSum += totalScore;
            }
            double moveAverage = moveSum / MOVE_TRIES;
            move.setValue(moveAverage);
            long endTime = System.currentTimeMillis();
            System.out.println(json.prettyPrint(move) + " SCORE: " + moveAverage);
            System.out.println("TIME: " + ((endTime - startTime) / 1000f));
        }

        moves.sort(new MoveValueComparator());
        System.out.println("SELECTED MOVE: " + json.prettyPrint(moves.get(0)) + " \nscore: " + moves.get(0).getValue());
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

            double difference = b.getValue() - a.getValue();
            if (difference > 0) {
                return 1;
            } else if (difference < 0) {
                return -1;
            }
            return 0;
        }
    }
}
