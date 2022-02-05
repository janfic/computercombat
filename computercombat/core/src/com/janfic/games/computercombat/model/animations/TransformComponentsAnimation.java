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
 * Animation transforming components to target components.
 *
 * @author Jan Fic
 */
public class TransformComponentsAnimation implements MoveAnimation {

    private List<Component> originalComponents, newComponents;

    public TransformComponentsAnimation() {
    }

    public TransformComponentsAnimation(List<Component> originalComponents, List<Component> newComponents) {
        this.originalComponents = originalComponents;
        this.newComponents = newComponents;
        assert (originalComponents.size() == newComponents.size());
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animation = new ArrayList<>();

        List<Action> transformAnimation = new ArrayList<>();
        for (int i = 0; i < originalComponents.size(); i++) {
            Component originalComponent = originalComponents.get(i);
            Component newComponent = newComponents.get(i);

            for (ComponentActor componentActor : screen.getBoard().getComponents()) {
                if (componentActor.getComponent().equals(originalComponent)) {
                    Action transformAction = Actions.sequence(Actions.fadeOut(0.25f * animationSpeed), Actions.run(() -> {
                        componentActor.setRegion(newComponent);
                    }), Actions.fadeIn(0.25f * animationSpeed));
                    transformAction.setActor(componentActor);
                    transformAnimation.add(transformAction);
                }
            }
        }
        animation.add(transformAnimation);
        return animation;
    }

    @Override
    public void write(Json json) {
        json.writeValue("originalComponents", this.originalComponents);
        json.writeValue("newComponents", this.newComponents);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.originalComponents = json.readValue("originalComponents", List.class, jsonData);
        this.newComponents = json.readValue("newComponents", List.class, jsonData);
    }

}
