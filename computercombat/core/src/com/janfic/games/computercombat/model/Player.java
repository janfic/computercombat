package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public abstract class Player implements Serializable, Cloneable {

    private String uid;
    private Deck deck;

    public Player() {
        this.uid = null;
        this.deck = null;
    }

    public Player(String uid, Deck deck) {
        this.uid = uid;
        this.deck = deck;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Deck getActiveDeck() {
        return deck;
    }

    public abstract void beginMatch(MatchState state, Player opponent);

    public abstract Move getMove();

    public abstract void updateState(List<MoveResult> state);

    public abstract void gameOver(MatchResults results);

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("uid", uid);
        json.writeValue("deck", deck);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.uid = json.readValue("uid", String.class, jsonData);
        this.deck = json.readValue("deck", Deck.class, jsonData);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            return ((Player) obj).getUID().equals(this.getUID());
        }
        return super.equals(obj);
    }

    public abstract Object clone() throws CloneNotSupportedException;
}
