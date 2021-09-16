package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class CollectAnimation implements MoveAnimation {

    private final Map<Integer, List<Component>> collected;
    private final List<Component> allComponents;

    public CollectAnimation(Map<Integer, List<Component>> collected) {
        this.collected = collected;
        this.allComponents = new ArrayList<>();
        for (Integer i : collected.keySet()) {
            allComponents.addAll(collected.get(i));
        }
    }

    @Override
    public List<List<Action>> animate(List<ComponentActor> componentActors, List<SoftwareActor> softwareActors) {
        List<List<Action>> actions = new ArrayList<>();
        return actions;
    }

    public List<Component> getAllComponents() {
        return allComponents;
    }

    public Map<Integer, List<Component>> getCollected() {
        return collected;
    }
}
