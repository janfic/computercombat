package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Card;
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
    private Map<Component, Card> progress;
    private List<Component> allComponents;
    private final static int[] gutterYs = new int[]{-22, 15, 73, 129, 187};

    public CollectAnimation(Map<Integer, List<Component>> collected, Map<Component, Card> progress) {
        this.collected = collected;
        this.progress = progress;
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
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, Board board, Map<String, List<SoftwareActor>> softwareActors, Map<String, ComputerActor> computerActors) {
        List<List<Action>> actions = new ArrayList<>();
        List<Action> popAction = new ArrayList<>();

        List<ComponentActor> componentActors = board.getComponents();
        for (Component component : getAllComponents()) {
            for (ComponentActor componentActor : componentActors) {
                if (componentActor.getComponent().equals(component)) {
                    boolean isPlayerMove = currentPlayerUID.equals(playerUID);
                    Action a = Actions.sequence(Actions.fadeOut(0.35f), Actions.delay(0.35f),
                            Actions.moveTo(isPlayerMove ? 0 : board.getWidth() - 7 - 24, (8 - (componentActor.getComponent().getY() + 1)) * 24 + 7, (isPlayerMove ? componentActor.getComponent().getX() : 7 - componentActor.getComponent().getX()) / 4f));
                    a.setActor(componentActor);
                    componentActor.setZIndex(0);
                    componentActor.getCollectedRegion().setVisible(true);
                    Action b = Actions.sequence(Actions.fadeOut(0), Actions.fadeIn(0.7f), Actions.fadeOut((isPlayerMove ? componentActor.getComponent().getX() : 7 - componentActor.getComponent().getX()) / 4f, Interpolation.fade));
                    b.setActor(componentActor.getCollectedRegion());
                    Image collectedComponent = new Image(board.getSkin(), "collected_component");

                    board.getStage().addActor(collectedComponent);
                    collectedComponent.setColor(board.getSkin().getColor(component.getTextureName().toUpperCase()).cpy().mul(1, 1, 1, 0));
                    collectedComponent.setZIndex(10);
                    Vector2 start = new Vector2(isPlayerMove ? -5 : board.getWidth() + 3, (8 - (componentActor.getComponent().getY() + 1)) * 24 + 7 + 12 - 4);
                    start = board.localToStageCoordinates(start);
                    Vector2 end = board.localToStageCoordinates(new Vector2(isPlayerMove ? -5 : board.getWidth() + 3, gutterYs[0]));
                    collectedComponent.setPosition(start.x, start.y);
                    Action c = Actions.sequence(
                            Actions.delay((isPlayerMove ? componentActor.getComponent().getX() : 7 - componentActor.getComponent().getX()) / 4f + 0.7f - 0.2f),
                            Actions.scaleTo(1, 4),
                            Actions.fadeIn(0.2f),
                            Actions.parallel(Actions.moveTo(end.x, end.y, Math.abs(end.y - start.y) / 70f, Interpolation.exp5), Actions.scaleTo(1, 1, Math.abs(end.y - start.y) / 70f)),
                            Actions.parallel(Actions.moveBy(isPlayerMove ? -13 : 13, 0, 0.2f, Interpolation.exp5), Actions.scaleTo(2, 1, 0.2f)),
                            Actions.fadeOut(0.1f),
                            Actions.removeActor()
                    );
                    c.setActor(collectedComponent);
                    popAction.add(a);
                    popAction.add(b);
                    popAction.add(c);
                }
            }
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
