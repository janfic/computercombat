package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
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
    private Player defensivePlayer;
    private String email;
    private List<Deck> decks;
    private Deck collection;
    private int packets;

    public Profile() {
    }

    public Profile(String uid) {
        this.uid = uid;
        this.decks = new ArrayList<>();
        this.collection = new Deck("Collection", 0);
        this.packets = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDefensivePlayer(Player defensivePlayer) {
        this.defensivePlayer = defensivePlayer;
    }

    public String getUID() {
        return uid;
    }

    public Player getDefensivePlayer() {
        return defensivePlayer;
    }

    public int getPackets() {
        return packets;
    }

    public void setPackets(int packets) {
        this.packets = packets;
    }

    public Deck getCollection() {
        return collection;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    @Override
    public void write(Json json) {
        json.writeValue("uid", this.uid);
        json.writeValue("name", this.name);
        json.writeValue("decks", this.decks);
        json.writeValue("collection", this.collection);
        json.writeValue("defensivePlayer", this.defensivePlayer);
        json.writeValue("packets", this.packets);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.uid = json.readValue("uid", String.class, jv);
        this.name = json.readValue("name", String.class, jv);
        this.decks = json.readValue("decks", List.class, jv);
        this.collection = json.readValue("collection", Deck.class, jv);
        this.defensivePlayer = json.readValue("defensivePlayer", Player.class, jv);
        this.packets = json.readValue("packets", Integer.class, jv) == null ? 0 : json.readValue("packets", Integer.class, jv);
    }
}
