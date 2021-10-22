package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class AttackAnimation implements MoveAnimation {

    Map<Card, List<Card>> attacks;

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen) {
        List<List<Action>> animations = new ArrayList<>();

        for (Card card : attacks.keySet()) {
            for (SoftwareActor softwareActor : screen.getSoftwareActors().get(currentPlayerUID)) {
                
            }
        }

        return animations;
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("attacks", attacks, Map.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.attacks = json.readValue("attacks", Map.class, jsonData);
    }

}
