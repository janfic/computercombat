package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class SwitchAnimation implements MoveAnimation {

    private Component a, b;

    public SwitchAnimation(Component a, Component b) {
        this.a = a;
        this.b = b;
    }

    private SwitchAnimation() {
        this.a = null;
        this.b = null;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen) {
        List<List<Action>> actions = new ArrayList<>();
        return actions;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("a", a);
        json.writeValue("b", b);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.a = json.readValue("a", Component.class, jv);
        this.b = json.readValue("b", Component.class, jv);
    }

}
