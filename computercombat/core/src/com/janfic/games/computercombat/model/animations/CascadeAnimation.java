package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class CascadeAnimation implements MoveAnimation {

    private List<CascadeData> cascade;

    public CascadeAnimation(List<CascadeData> cascade) {
        this.cascade = cascade;
    }

    private CascadeAnimation() {
        this.cascade = null;
    }

    @Override
    public List<List<Action>> animate(List<ComponentActor> componentActors, List<SoftwareActor> softwareActors) {
        List<List<Action>> actions = new ArrayList<>();
        return actions;
    }

    public List<CascadeData> getCascade() {
        return cascade;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("cascade", cascade);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.cascade = json.readValue("cascade", List.class, jv);
    }

    public static class CascadeData implements Json.Serializable {

        Component originalComponent;
        Component fallenComponent;

        public CascadeData(Component fallenComponent, int ox, int oy) {
            Json json = new Json();
            int fx = fallenComponent.getX();
            int fy = fallenComponent.getY();
            fallenComponent.setPosition(ox, oy);
            originalComponent = json.fromJson(Component.class, json.toJson(fallenComponent));
            fallenComponent.setPosition(fx, fy);
            this.fallenComponent = fallenComponent;
        }

        public CascadeData() {
        }

        public Component getFallenComponent() {
            return fallenComponent;
        }

        public Component getOriginalComponent() {
            return originalComponent;
        }

        @Override
        public void write(Json json) {
            json.writeType(this.getClass());
            json.writeValue("originalComponent", this.originalComponent);
            json.writeValue("fallenComponent", this.fallenComponent);
        }

        @Override
        public void read(Json json, JsonValue jv) {
            this.originalComponent = json.readValue("originalComponent", Component.class, jv);
            this.fallenComponent = json.readValue("fallenComponent", Component.class, jv);
        }

    }
}
