package com.janfic.games.computercombat.model;

import java.util.ArrayList;
import java.util.Arrays;
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
        this.componentBoard = Arrays.copyOf(state.componentBoard, state.componentBoard.length);
        this.activeEntities = Map.copyOf(state.activeEntities);
        this.computers = Map.copyOf(state.computers);
        this.decks = Map.copyOf(state.decks);
        this.currentPlayerMove = state.currentPlayerMove;
        this.players = List.copyOf(state.players);
    }

    public MatchState(MatchState state, String playerUID) {
        this(state);
        for (Player player : players) {
            this.decks.remove(player);
        }
    }
}
