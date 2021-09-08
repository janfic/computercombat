package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.data.Deck;
import com.janfic.games.computercombat.model.players.HumanPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class Profile implements Serializable {

    private String uid;
    private String name;
    private String activePlayer;
    private List<Deck> decks;
    private Deck collection;

    private Profile() {
        this("defaultUID");
    }

    public Profile(String uid) {
        this.uid = uid;
        this.decks = new ArrayList<>();
        this.collection = new Deck("Collection");
        this.activePlayer = HumanPlayer.class.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
    }

    public String getUID() {
        return uid;
    }

    public Player getActivePlayer() {
        System.out.println(uid);
        try {
            Player p = (Player) Class.forName(activePlayer).getConstructor(String.class, SoftwareDeck.class, Computer.class).newInstance(uid, buildDeck(), buildComputer());
            System.out.println(p.getUID());
            return p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Deck getCollection() {
        return collection;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    private SoftwareDeck buildDeck() {
        return null;
    }

    private Computer buildComputer() {
        return null;
    }

    @Override
    public void write(Json json) {
        json.writeValue("uid", this.uid);
        json.writeValue("name", this.name);
        json.writeValue("decks", this.decks);
        json.writeValue("collection", this.collection);
        json.writeValue("activePlayer", this.activePlayer);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.uid = json.readValue("uid", String.class, jv);
        this.name = json.readValue("name", String.class, jv);
        this.decks = json.readValue("decks", List.class, jv);
        this.collection = json.readValue("collection", Deck.class, jv);
        this.activePlayer = json.readValue("activePlayer", String.class, jv);
        System.out.println(activePlayer);
    }
}
