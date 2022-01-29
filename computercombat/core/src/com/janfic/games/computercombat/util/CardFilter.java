package com.janfic.games.computercombat.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Card;

/**
 *
 * @author Jan Fic
 */
public abstract class CardFilter implements Serializable, Filter<Card> {

    String description;

    public CardFilter() {
    }

    public CardFilter(String description) {
        this.description = description;
    }
    
    public abstract boolean filter(Card card);

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.description = json.readValue("description", String.class, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeValue("description", this.description);
    }

}
