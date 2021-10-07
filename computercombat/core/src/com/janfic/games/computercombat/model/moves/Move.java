package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.MatchState;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public abstract class Move implements Json.Serializable {

    protected String playerUID;

    public Move() {
        this.playerUID = null;
    }

    public Move(String playerUID) {
        this.playerUID = playerUID;
    }

    public abstract List<MoveResult> doMove(MatchState state);

    public String getPlayerUID() {
        return playerUID;
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
