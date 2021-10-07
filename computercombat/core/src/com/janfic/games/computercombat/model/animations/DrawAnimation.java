package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class DrawAnimation implements MoveAnimation {

    String playerUID;
    List<Software> newSoftware;

    public DrawAnimation(String playerUID, List<Software> newSoftware) {
        this.playerUID = playerUID;
        this.newSoftware = newSoftware;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, Board board, Map<String, List<SoftwareActor>> softwareActors, Map<String, ComputerActor> computerActors) {
        return new ArrayList<>();
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("playerUID", playerUID);
        json.writeValue("newSoftware", newSoftware);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.newSoftware = json.readValue("newSoftware", List.class, jsonData);
    }
}
