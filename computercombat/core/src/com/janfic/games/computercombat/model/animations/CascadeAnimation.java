package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {

        Board board = screen.getBoard();
        List<List<Action>> actions = new ArrayList<>();
        List<Action> cascadeAnimation = new ArrayList<>();

        Map<Integer, List<CascadeData>> newComponentFallingOrder = new HashMap<>();
        for (CascadeData cas : cascade) {
            boolean found = false;
            for (Cell<ComponentActor>[] cells : board.getBoard()) {
                for (Cell<ComponentActor> cell : cells) {
                    if (cell.getActor().getComponent().equals(cas.getOriginalComponent())) {
                        int x = cas.getFallenComponent().getX();
                        int y = cas.getFallenComponent().getY();
                        Action moveAction = Actions.moveTo(
                                24 * x + 7,
                                (8 - (cas.getFallenComponent().getY() + 1)) * 24 + 7,
                                ((cas.getFallenComponent().getY() - cas.getOriginalComponent().getY()) / 4f) * animationSpeed, Interpolation.pow2In);
                        moveAction.setActor(cell.getActor());
                        cascadeAnimation.add(moveAction);
                        found = true;
                    }
                }
            }
            if (!found) {
                List<CascadeData> columnFall = newComponentFallingOrder.getOrDefault(cas.getOriginalComponent().getX(), new ArrayList<>());
                columnFall.add(cas);
                newComponentFallingOrder.put(cas.getOriginalComponent().getX(), columnFall);
            }
        }

        Group newSpawn = board.getNewComponentSpawn().getActor();
        for (int x = 0; x < board.getBoard().length; x++) {
            List<CascadeData> columnFall = newComponentFallingOrder.get(x);
            if (columnFall == null) {
                continue;
            }
            columnFall.sort(new Comparator<CascadeData>() {
                @Override
                public int compare(CascadeData o1, CascadeData o2) {
                    return o2.getFallenComponent().getY() - o1.getFallenComponent().getY();
                }
            });
            for (int i = 0; i < columnFall.size(); i++) {
                ComponentActor component = new ComponentActor(columnFall.get(i).getFallenComponent());
                newSpawn.addActor(component);
                component.setPosition(24 * x, 3000);
                Action move = Actions.sequence(
                        Actions.moveTo(24 * x, i * 24),
                        Actions.moveTo(
                                24 * x,
                                -(columnFall.get(i).getFallenComponent().getY() + 1) * 24,
                                ((columnFall.get(i).getFallenComponent().getY() - (-1 - i)) / 4f) * animationSpeed,
                                Interpolation.pow2In)
                );
                move.setActor(component);
                cascadeAnimation.add(move);
            }
        }
        actions.add(cascadeAnimation);
        return actions;
    }

    public List<CascadeData> getCascade() {
        return cascade;
    }

    @Override
    public void write(Json json) {
        json.writeValue("cascade", cascade);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.cascade = json.readValue("cascade", List.class,
                jv);
    }

    public static class CascadeData implements Json.Serializable {

        Component originalComponent;
        Component fallenComponent;

        public CascadeData(Component fallenComponent, Component originalComponent) {
            this.originalComponent = originalComponent;
            this.fallenComponent = fallenComponent;
        }

        public CascadeData(Component fallenComponent, int originalX, int originalY) {
            Json json = new Json();
            int fx = fallenComponent.getX();
            int fy = fallenComponent.getY();
            fallenComponent.setPosition(originalX, originalY);
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
