package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.Move;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class MatchData {

    Profile player1, player2;
    Deck player1Deck, player2Deck;
    List<Move> moves;
    List<MatchState> matchStates;
    List<List<MoveResult>> moveResults;
    boolean winner;
    Timestamp starttime, endtime;

    public MatchData(Profile player1, Profile player2, Deck player1Deck, Deck player2Deck) {
        this.moves = new ArrayList<>();
        this.matchStates = new ArrayList<>();
        this.moveResults = new ArrayList<>();
        this.player1 = player1;
        this.player2 = player2;
        this.player1Deck = player1Deck;
        this.player2Deck = player2Deck;
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

    public Profile getPlayer1() {
        return player1;
    }

    public Profile getPlayer2() {
        return player2;
    }

    public boolean getWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public Deck getPlayer1Deck() {
        return player1Deck;
    }

    public Deck getPlayer2Deck() {
        return player2Deck;
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
}
