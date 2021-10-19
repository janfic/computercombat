package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class SpawnAnimation implements MoveAnimation {

    List<Component> oldComponents;
    List<Component> spawned;

    public SpawnAnimation() {
        this.oldComponents = null;
        this.spawned = null;
    }

    public SpawnAnimation(List<Component> oldComponents, List<Component> spawned) {
        this.oldComponents = oldComponents;
        this.spawned = spawned;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen) {
        List<List<Action>> animation = new ArrayList<>();

        List<Action> remove = new ArrayList<>();
        for (Component oldComponent : oldComponents) {
            Action fade = Actions.fadeOut(1);
            fade.setActor(screen.getBoard().getBoard()[oldComponent.getX()][oldComponent.getY()].getActor());
            remove.add(fade);
        }

        List<Action> spawn = new ArrayList<>();
        for (Component component : spawned) {
            ComponentActor componentActor = new ComponentActor(component);
            componentActor.setVisible(false);
            screen.getBoard().getBoard()[component.getX()][component.getY()].setActor(componentActor);
            Action spawnAction = Actions.sequence(Actions.scaleTo(1.5f, 1.5f), Actions.alpha(0), Actions.visible(true), Actions.parallel(Actions.scaleTo(1, 1, 1), Actions.fadeIn(1)));
            spawnAction.setActor(componentActor);
            spawn.add(spawnAction);
        }

        animation.add(remove);
        animation.add(spawn);
        return animation;
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("spawned", spawned, List.class);
        json.writeValue("oldComponents", oldComponents, List.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.spawned = json.readValue("spawned", List.class, jsonData);
        this.oldComponents = json.readValue("oldComponents", List.class, jsonData);
    }

}
