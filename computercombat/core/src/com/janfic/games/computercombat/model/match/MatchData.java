package com.janfic.games.computercombat.model.match;

import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.animations.CollectAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MatchData {

    Player player1, player2;
    List<Move> moves;
    List<MatchState> matchStates;
    List<List<MoveResult>> moveResults;
    boolean winner;
    Timestamp starttime, endtime;
    Map<String, Integer> rewards;

    public MatchData(Player player1, Player player2) {
        this.rewards = new HashMap<>();
        rewards.put(player1.getUID(), 0);
        rewards.put(player2.getUID(), 0);
        this.moves = new ArrayList<>();
        this.matchStates = new ArrayList<>();
        this.moveResults = new ArrayList<List<MoveResult>>() {
            @Override
            public boolean add(List<MoveResult> e) {
                for (MoveResult moveResult : e) {
                    for (MoveAnimation animation : moveResult.getAnimations()) {
                        if (animation instanceof CollectAnimation) {
                            CollectAnimation collect = (CollectAnimation) animation;
                            for (Component component : collect.getAllComponents()) {
                                if (component.getTextureName().equals("network")) {
                                    rewards.put(moveResult.getMove().getPlayerUID(), rewards.getOrDefault(moveResult.getMove().getPlayerUID(), 0) + 1);
                                }
                            }
                        }
                    }
                }
                return super.add(e);
            }
        };
        this.player1 = player1;
        this.player2 = player2;
    }

    public void add(Move move, List<MoveResult> results, MatchState state) {
        this.moves.add(move);
        this.moveResults.add(results);
        this.matchStates.add(state);
    }

    public List<MatchState> getMatchStates() {
        return matchStates;
    }

    public List<List<MoveResult>> getMoveResults() {
        return moveResults;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2; 
    }

    public boolean getWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public void setStartTime(Timestamp time) {
        this.starttime = time;
    }

    public void setEndTime(Timestamp time) {
        this.endtime = time;
    }

    public Timestamp getStartTime() {
        return starttime;
    }

    public Timestamp getEndTime() {
        return endtime;
    }

    public Map<String, Integer> getRewards() {
        return rewards;
    }
}
