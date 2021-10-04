package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class TranslationAnimation implements MoveAnimation {

    private TranslationAnimation() {
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, Board board, Map<String, List<SoftwareActor>> softwareActors, Map<String, ComputerActor> computerActors) {
        List<List<Action>> animation = new ArrayList<>();
        return animation;
    }

    @Override
    public void write(Json json) {
    }

    @Override
    public void read(Json json, JsonValue jv) {
    }

}
