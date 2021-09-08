package com.janfic.games.computercombat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class MatchState {

    public final Component[][] componentBoard;
    public final Map<Player, List<Card>> activeEntities;
    public final Map<Player, Computer> computers;
    public final Map<Player, SoftwareDeck> decks;
    public final List<Player> players;
    public final Player currentPlayerMove;

    public MatchState() {
        this.componentBoard = null;
        this.activeEntities = null;
        this.computers = null;
        this.decks = null;
        this.players = null;
        this.currentPlayerMove = null;
    }

    public MatchState(Player player1, Player player2, Component[][] componentBoard, Map<Player, List<Card>> activeEntities, Map<Player, Computer> computers, Map<Player, SoftwareDeck> decks) {
        this.componentBoard = componentBoard;
        this.activeEntities = activeEntities;
        this.computers = computers;
        this.decks = decks;
        this.currentPlayerMove = player1;
        this.players = new ArrayList<>();
        this.players.add(player1);
        this.players.add(player2);
    }

    public Component[][] getComponentBoard() {
        return componentBoard;
    }

    public MatchState(MatchState state) {
        this.componentBoard = new Component[state.componentBoard.length][state.componentBoard[0].length];
        this.activeEntities = new HashMap<>();
        this.computers = new HashMap<>();
        this.decks = new HashMap<>();
        this.currentPlayerMove = state.currentPlayerMove;
        this.players = new ArrayList<>();

        for (int x = 0; x < state.componentBoard.length; x++) {
            for (int y = 0; y < state.componentBoard[x].length; y++) {
                this.componentBoard[x][y] = state.componentBoard[x][y];
            }
        }

        for (Player key : state.activeEntities.keySet()) {
            this.activeEntities.put(key, state.activeEntities.get(key));
        }

        for (Player key : state.computers.keySet()) {
            this.computers.put(key, state.computers.get(key));
        }

        for (Player key : state.decks.keySet()) {
            this.decks.put(key, state.decks.get(key));
        }

        for (Player player : state.players) {
            this.players.add(player);
        }
    }

    public MatchState(MatchState state, String playerUID) {
        this(state);
        for (Player player : players) {
            this.decks.remove(player);
        }
    }
}
