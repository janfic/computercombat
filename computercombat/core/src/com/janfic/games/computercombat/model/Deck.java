package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class Deck implements Serializable {

    private String name;
    private Map<Card, Integer> cards;

    public Deck() {
        this.name = "New Deck";
        this.cards = new HashMap<>();
    }

    public Deck(String name) {
        this.name = name;
        this.cards = new HashMap<>();
    }

    public void addCard(Software card, int amount) {
        cards.put(card, cards.getOrDefault(card, 0) + amount);
    }

    public void removeCard(Card card, int amount) {
        int a = cards.getOrDefault(card, 0) - amount;
        if (a > 0) {
            cards.put(card, a);
        } else {
            cards.remove(card);
        }
    }

    public int getCardCount(String card) {
        return cards.getOrDefault(card, 0);
    }

    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards.keySet());
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", this.name);
        json.writeValue("cards", this.cards);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.name = json.readValue("name", String.class, jv);
        this.cards = json.readValue("cards", HashMap.class, jv);
    }

}
