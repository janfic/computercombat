package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class Deck implements Serializable, Cloneable {

    private String name;
    private int id;
    private Map<String, Integer> cards;
    private List<Integer> stack;

    public Deck() {
        this.name = "New Deck";
        this.cards = new HashMap<>();
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

    public void addCard(Card card, int amount) {
        cards.put("" + card.getID(), cards.getOrDefault("" + card.getID(), 0) + amount);
        for (int i = 0; i < amount; i++) {
            this.stack.add(card.getID());
        }
    }

    public void removeCard(Card card, int amount) {
        int a = cards.getOrDefault("" + card.getID(), 0) - amount;
        if (a > 0) {
            cards.put("" + card.getID(), a);
        } else {
            cards.remove("" + card.getID());
        }
        for (int i = 0; i < amount; i++) {
            stack.remove((Integer) card.getID());
        }
    }

    public int getCardCount(Integer cardID) {
        if (cardID >= 0) {
            return cards.getOrDefault("" + cardID, 0);
        } else {
            int amount = 0;
            for (String string : cards.keySet()) {
                amount += cards.get(string);
            }
            return amount;
        }
    }

    public String getName() {
        return name;
    }

    public List<Integer> getStack() {
        return stack;
    }

    public Map<String, Integer> getCards() {
        return cards;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Card draw() {
        Card r = SQLAPI.getSingleton().getCardById(stack.get(0), null);
        r.generateMatchID();
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
        json.writeArrayStart("cards");
        for (String string : cards.keySet()) {
            json.writeValue(string);
            json.writeValue(cards.get(string));
        }
        json.writeArrayEnd();
        //json.writeValue("cards", this.cards);
//        json.writeValue("stack", this.stack);
        json.writeArrayStart("stack");
        for (Integer integer : stack) {
            json.writeValue(integer);
        }
        json.writeArrayEnd();
        json.writeValue("id", this.id);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.name = json.readValue("name", String.class, jv);
        this.id = (json.readValue("id", int.class, jv) == null ? 0 : json.readValue("id", int.class, jv));
        int[] cardData = json.readValue("cards", int[].class, jv);
        this.cards = new HashMap<>();
        if (cardData != null) {
            for (int i = 0; i < cardData.length; i += 2) {
                this.cards.put("" + cardData[i], cardData[i + 1]);
            }
        }
        int[] stackData = json.readValue("stack", int[].class, jv);
        this.stack = new ArrayList<>();
        if (stackData != null) {
            for (int i : stackData) {
                this.stack.add(i);
            }
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Deck) {
            Deck o = (Deck) obj;
            return o.getID() == this.getID();
        }
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Deck clone = new Deck(this.name, this.id);
        for (String string : cards.keySet()) {
            clone.cards.put(string, this.cards.get(string));
        }
        for (Integer integer : stack) {
            clone.stack.add(integer);
        }
        return clone;
    }

}
