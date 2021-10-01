package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
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
    public final Map<Profile, List<Card>> activeEntities;
    public final Map<Profile, Computer> computers;
    public final Map<Profile, SoftwareDeck> decks;
    public final List<Profile> players;
    public Profile currentPlayerMove;

    public MatchState() {
        this.componentBoard = null;
        this.activeEntities = null;
        this.computers = null;
        this.decks = null;
        this.players = null;
        this.currentPlayerMove = null;
    }

    public MatchState(Profile player1, Profile player2, Component[][] componentBoard, Map<Profile, List<Card>> activeEntities, Map<Profile, Computer> computers, Map<Profile, SoftwareDeck> decks) {
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
        Json json = new Json();

        MatchState s = json.fromJson(MatchState.class, json.toJson(state));
        this.componentBoard = s.componentBoard;
        this.activeEntities = s.activeEntities;
        this.computers = s.computers;
        this.decks = s.decks;
        this.currentPlayerMove = s.currentPlayerMove;
        this.players = s.players;
    }

    public MatchState(MatchState state, String playerUID) {
        this(state);
        for (Profile player : players) {
            this.decks.remove(player);
        }
    }

    public String toStringBoard() {
        String s = "";
        for (int y = 0; y < componentBoard[0].length; y++) {
            for (int x = 0; x < componentBoard.length; x++) {
                if (componentBoard[x][y] != null) {
                    s += componentBoard[x][y].getTextureName() + " , ";
                } else {
                    s += "null , ";
                }
            }
            s += "\n";
        }
        return s;
    }

    public Profile getOtherProfile(Profile profile) {
        for (Profile player : players) {
            if (!player.getUID().equals(profile.getUID())) {
                return player;
            }
        }
        return null;
    }

    public List<Component> getComponentsAsList() {
        List<Component> components = new ArrayList<>();
        for (Component[] cr : componentBoard) {
            for (Component c : cr) {
                components.add(c);
            }
        }
        return components;
    }
}
