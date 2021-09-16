package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.MatchState;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public abstract class Move implements Serializable {

    protected String playerUID;

    public Move() {
    }

    public Move(String playerUID) {
        this.playerUID = playerUID;
    }

    public abstract List<MoveResult> doMove(MatchState state);

    public String getPlayerUID() {
        return playerUID;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("playerUID", playerUID);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.playerUID = json.readValue("playerUID", String.class, jv);
    }
}
