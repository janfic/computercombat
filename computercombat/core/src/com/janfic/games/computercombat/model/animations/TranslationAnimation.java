package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
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
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen) {
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
