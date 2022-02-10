package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;

/**
 *
 * @author Jan Fic
 */
public abstract class Player implements Serializable {

    private String uid;
    private Deck activeDeck;

    public Player() {
        this.uid = null;
    }

    public Player(String uid, Deck activeDeck) {
        this.uid = uid;
        this.activeDeck = activeDeck;
    }

    public String getUID() {
        return uid;
    }

    public abstract void beginMatch(MatchState state, Player opponent);

    public abstract Move getMove();

    public abstract void updateState(MatchState state);

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("uid", uid);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.uid = json.readValue("uid", String.class, jsonData);
    }
}
