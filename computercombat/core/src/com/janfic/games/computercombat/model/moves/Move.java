package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.abilities.AttackAbility;
import com.janfic.games.computercombat.model.animations.CascadeAnimation;
import com.janfic.games.computercombat.model.animations.CollectAnimation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public abstract class Move implements Json.Serializable {

    protected String playerUID;
    private double value;

    public Move() {
        this.playerUID = null;
        this.value = 0;
    }

    public Move(String playerUID) {
        this.playerUID = playerUID;
        this.value = 0;
    }

    public abstract List<MoveResult> doMove(MatchState state);

    public String getPlayerUID() {
        return playerUID;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("playerUID", playerUID);
    }
}
