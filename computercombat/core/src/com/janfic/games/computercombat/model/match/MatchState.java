package com.janfic.games.computercombat.model.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Profile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class MatchState implements Serializable {

    public Component[][] componentBoard;
    public Map<String, List<Card>> activeEntities;
    public Map<String, Computer> computers;
    public Map<String, Deck> decks;
    public List<Profile> players;
    public Profile currentPlayerMove;
    public boolean isGameOver;
    public Profile winner;

    public MatchState() {
        this.componentBoard = null;
        this.activeEntities = null;
        this.computers = null;
        this.decks = null;
        this.players = null;
        this.currentPlayerMove = null;
        this.isGameOver = false;
        this.winner = null;
    }

    public MatchState(Profile player1, Profile player2, Component[][] componentBoard, Map<String, List<Card>> activeEntities, Map<String, Computer> computers, Map<String, Deck> decks) {
        this.componentBoard = componentBoard;
        this.activeEntities = activeEntities;
        this.computers = computers;
        this.decks = decks;
        this.currentPlayerMove = player1;
        this.players = new ArrayList<>();
        this.players.add(player1);
        this.players.add(player2);
        this.isGameOver = false;
        this.winner = null;
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
        this.winner = s.winner;
        this.isGameOver = s.isGameOver;
    }

    public MatchState(MatchState state, String playerUID) {
        this(state);
        for (Profile player : players) {
            this.decks.remove(player.getUID());
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

    @Override
    public void write(Json json) {
        json.writeValue("players", players, List.class);
        json.writeValue("currentPlayerMove", currentPlayerMove, Profile.class);
        json.writeValue("activeEntities", activeEntities, Map.class);
        json.writeValue("computers", computers, Map.class);
        json.writeValue("decks", decks, Map.class);
        json.writeValue("winner", winner, Profile.class);
        json.writeValue("isGameOver", isGameOver, boolean.class);
        String board = "";
        for (Component[] components : componentBoard) {
            for (Component component : components) {
                board += "" + Component.componentToNumber.get(component.getClass());
            }
        }
        assert (board.length() == 64);
        json.writeValue("componentBoard", board);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.players = json.readValue("players", List.class, jsonData);
        this.currentPlayerMove = json.readValue("currentPlayerMove", Profile.class, jsonData);
        this.isGameOver = json.readValue("isGameOver", boolean.class, jsonData);
        this.winner = json.readValue("winner", Profile.class, jsonData);
        this.activeEntities = json.readValue("activeEntities", HashMap.class, List.class, jsonData);
        this.computers = json.readValue("computers", HashMap.class, Computer.class, jsonData);
        this.decks = json.readValue("decks", HashMap.class, Deck.class, jsonData);
        String boardString = json.readValue("componentBoard", String.class, jsonData);
        //int[][] comps = json.readValue("componentBoard", int[][].class, jsonData);
        componentBoard = new Component[8][8];
//        for (int x = 0; x < comps.length; x++) {
//            for (int y = 0; y < comps[x].length; y++) {
//                try {
//                    componentBoard[x][y] = (Component) Component.numberToComponent.get(comps[x][y]).getConstructor(int.class, int.class).newInstance(x, y);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        assert (boardString.length() == 64);
        for (int i = 0; i < boardString.length(); i++) {
            int x = i / 8;
            int y = i % 8;
            System.out.println(x + " " + y);
            try {
                componentBoard[x][y] = (Component) Component.numberToComponent.get(
                        Integer.parseInt("" + boardString.substring(i, i + 1)))
                        .getConstructor(int.class, int.class).newInstance(x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
