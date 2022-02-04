package com.janfic.games.computercombat.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;

/**
 *
 * @author Jan Fic
 */
public abstract class ComponentFilter implements Serializable, Filter<Component> {

    /*
        new CollectAbility([new ComponentFilter("Select 1 Blue Gem"){
            public boolean filter(Component c) {
                return c.type == storageComponent;
            }
        }])
     */
    String description;

    public ComponentFilter() {
    }

    public ComponentFilter(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract boolean filter(Component component, MatchState state, Move move);

    @Override
    public void write(Json json) {
        json.writeValue("description", this.description);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.description = json.readValue("description", String.class, jsonData);
    }
}
