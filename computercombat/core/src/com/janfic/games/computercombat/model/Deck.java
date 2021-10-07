package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class Deck implements Serializable {

    private String name;
    private int id;
    private Map<String, Integer> cards;
    private List<Software> stack;

    public Deck() {
        this.name = "New Deck";
        this.cards = new HashMap<>();
        this.id = (int) (Math.random() * Integer.MAX_VALUE);
        this.stack = new ArrayList<>();
    }

    public Deck(String name) {
        this.name = name;
        this.cards = new HashMap<>();
        this.id = (int) (Math.random() * Integer.MAX_VALUE);
        this.stack = new ArrayList<>();
    }

    public Deck(String name, int id) {
        this.name = name;
        this.id = id;
        this.cards = new HashMap<>();
        this.stack = new ArrayList<>();
    }

    public int getID() {
        return id;
    }

    public void addCard(Software card, int amount) {
        cards.put("" + card.getID(), cards.getOrDefault("" + card.getID(), 0) + amount);
        for (int i = 0; i < amount; i++) {
            this.stack.add(card);
        }
    }

    public void removeCard(Software card, int amount) {
        int a = cards.getOrDefault("" + card.getID(), 0) - amount;
        if (a > 0) {
            cards.put("" + card.getID(), a);
        } else {
            cards.remove("" + card.getID());
        }
        for (int i = 0; i < amount; i++) {
            stack.remove(card);
        }
    }

    public int getCardCount(Software card) {
        return cards.getOrDefault("" + card.getID(), 0);
    }

    public String getName() {
        return name;
    }

    public List<Software> getCards() {
        return stack;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Software draw() {
        Software r = stack.get(0);
        stack.remove(0);
        int amount = cards.get("" + r.getID());
        if (amount == 1) {
            cards.remove("" + r.getID());
        } else {
            cards.put("" + r.getID(), amount - 1);
        }
        return r;
    }

    public void shuffle() {
        Collections.shuffle(stack);
    }

    public int size() {
        return cards.size();
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", this.name);
        json.writeValue("cards", this.cards);
        json.writeValue("stack", this.stack);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.name = json.readValue("name", String.class, jv);
        this.cards = json.readValue("cards", HashMap.class, jv);
        this.stack = json.readValue("stack", List.class, jv);
    }

}
