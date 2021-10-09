package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.CollectionCard;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class DrawAnimation implements MoveAnimation {

    String playerUID;
    List<Software> newSoftware;

    public DrawAnimation() {
    }

    public DrawAnimation(String playerUID, List<Software> newSoftware) {
        this.playerUID = playerUID;
        this.newSoftware = newSoftware;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, Board board, Map<String, List<SoftwareActor>> softwareActors, Map<String, ComputerActor> computerActors) {

        List<List<Action>> animations = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        ComputerCombatGame game = (ComputerCombatGame) Gdx.app.getApplicationListener();
        if (newSoftware.size() > 0) {
            Table t = new Table();
            t.setFillParent(true);
            CollectionCard card = new CollectionCard(game, board.getSkin(), newSoftware.get(0), 1);
            card.setTouchable(Touchable.disabled);
            Stage stage = board.getStage();
            stage.addActor(t);
            card.setVisible(false);
            card.setPosition(stage.getWidth() / 2, -300);
            t.add(card).expand().center();
            Action cardAction = Actions.sequence(Actions.moveBy(0, -400), Actions.visible(true), Actions.moveBy(0, 400, 1, Interpolation.fastSlow), Actions.delay(1), Actions.moveBy(0, -400, 1, Interpolation.slowFast));
            actions.add(cardAction);
            cardAction.setActor(card);
        }
        animations.add(actions);
        return animations;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("playerUID", playerUID);
        json.writeValue("newSoftware", newSoftware);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.newSoftware = json.readValue("newSoftware", List.class, jsonData);
    }
}
