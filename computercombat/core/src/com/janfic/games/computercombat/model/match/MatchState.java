package com.janfic.games.computercombat.model.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.util.CardFilter;
import com.janfic.games.computercombat.util.ComponentFilter;
import com.janfic.games.computercombat.util.NullifyingJson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 *
 * @author Jan Fic
 */
public class MatchState implements Serializable {

    public Component[][] componentBoard;
    public Map<String, List<Card>> activeEntities;
    public Map<String, Computer> computers;
    public Map<String, Deck> decks;
    public List<Player> players;
    public Player currentPlayerMove;
    public boolean isGameOver;
    public Player winner;

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

    public MatchState(Player player1, Player player2, Component[][] componentBoard, Map<String, List<Card>> activeEntities, Map<String, Computer> computers, Map<String, Deck> decks) {
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
        Json json = new NullifyingJson();
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
        for (Player player : players) {
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

    public Player getOtherProfile(Player profile) {
        for (Player player : players) {
            if (!player.getUID().equals(profile.getUID())) {
                return player;
            }
        }
        return null;
    }

    public Player getOtherProfile(String uid) {
        for (Player player : players) {
            if (!player.getUID().equals(uid)) {
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
        json.writeValue("currentPlayerMove", currentPlayerMove, Player.class);
        json.writeValue("activeEntities", activeEntities, Map.class);
        json.writeValue("computers", computers, Map.class);
        json.writeValue("decks", decks, Map.class);
        json.writeValue("winner", winner, Player.class);
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
        this.currentPlayerMove = json.readValue("currentPlayerMove", Player.class, jsonData);
        this.isGameOver = json.readValue("isGameOver", boolean.class, jsonData);
        this.winner = json.readValue("winner", Player.class, jsonData);
        this.activeEntities = json.readValue("activeEntities", HashMap.class, List.class, jsonData);
        this.computers = json.readValue("computers", HashMap.class, Computer.class, jsonData);
        this.decks = json.readValue("decks", HashMap.class, Deck.class, jsonData);
        String boardString = json.readValue("componentBoard", String.class, jsonData);
        componentBoard = new Component[8][8];
        assert (boardString.length() == 64);
        for (int i = 0; i < boardString.length(); i++) {
            int x = i / 8;
            int y = i % 8;
            try {
                componentBoard[x][y] = (Component) Component.numberToComponent.get(
                        Integer.parseInt("" + boardString.substring(i, i + 1)))
                        .getConstructor(int.class, int.class).newInstance(x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int countComponents(ComponentFilter filter, Move move) {
        int count = 0;
        for (Component[] components : componentBoard) {
            for (Component component : components) {
                if (filter.filter(component, this, move)) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Component> getComponentsByFilter(ComponentFilter filter, Move move) {
        List<Component> list = getComponentsAsList();
        list.removeIf(new Predicate<Component>() {
            @Override
            public boolean test(Component t) {
                return !filter.filter(t, MatchState.this, move);
            }
        });
        return list;
    }

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();
        for (String key : activeEntities.keySet()) {
            cards.addAll(activeEntities.get(key));
        }
        return cards;
    }

    public List<Card> getCardsByFilter(CardFilter filter, Move move) {
        List<Card> cards = getAllCards();
        cards.removeIf(new Predicate<Card>() {
            @Override
            public boolean test(Card t) {
                return !filter.filter(t, MatchState.this, move);
            }
        });
        return cards;
    }

}
