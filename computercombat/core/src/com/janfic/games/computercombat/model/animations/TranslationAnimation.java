package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class TranslationAnimation implements MoveAnimation {

    private TranslationAnimation() {
    }

    @Override
    public List<List<Action>> animate(Board board, List<SoftwareActor> softwareActors) {
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
