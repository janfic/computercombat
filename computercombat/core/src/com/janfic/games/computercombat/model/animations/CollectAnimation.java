package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
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

    private Map<Integer, List<Component>> collected;
    private List<Component> allComponents;

    public CollectAnimation(Map<Integer, List<Component>> collected) {
        this.collected = collected;
        this.allComponents = new ArrayList<>();
        for (Integer i : collected.keySet()) {
            allComponents.addAll(collected.get(i));
        }
    }

    private CollectAnimation() {
        this.collected = null;
        this.allComponents = null;
    }

    @Override
    public List<List<Action>> animate(Board board, List<SoftwareActor> softwareActors) {
        List<List<Action>> actions = new ArrayList<>();
        List<Action> popAction = new ArrayList<>();

        List<ComponentActor> componentActors = board.getComponents();
        for (Component component : getAllComponents()) {
            for (ComponentActor componentActor : componentActors) {
                if (componentActor.getComponent().equals(component)) {
                    Action a = Actions.parallel(Actions.fadeOut(0.35f));
                    a.setActor(componentActor);
                    componentActor.getCollectedRegion().setVisible(true);
                    Action b = Actions.sequence(Actions.fadeOut(0), Actions.fadeIn(0.35f));
                    b.setActor(componentActor.getCollectedRegion());
                    popAction.add(a);
                    popAction.add(b);
                    System.out.println("found collected component actor: " + component);
                }
            }
        }
        for (Action action : popAction) {
            System.out.println(action);
        }
        actions.add(popAction);
        return actions;
    }

    public List<Component> getAllComponents() {
        return allComponents;
    }

    public Map<Integer, List<Component>> getCollected() {
        return collected;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("collected", this.collected, Map.class);
        json.writeValue("allComponents", this.allComponents, List.class);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.allComponents = json.readValue("allComponents", List.class, jv);
        this.collected = json.readValue("collected", Map.class, jv);
    }
}
