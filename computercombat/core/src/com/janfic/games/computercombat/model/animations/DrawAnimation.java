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
import com.janfic.games.computercombat.actors.CollectionCard;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DrawAnimation implements MoveAnimation {

    String playerUID;
    List<Card> newSoftware;

    public DrawAnimation() {
    }

    public DrawAnimation(String playerUID, List<Card> newSoftware) {
        this.playerUID = playerUID;
        this.newSoftware = newSoftware;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        Table leftPanel = screen.getLeftPanel();
        Table rightPanel = screen.getRightPanel();
        Table panel;
        if (playerUID.equals(currentPlayerUID)) {
            panel = leftPanel;
        } else {
            panel = rightPanel;
        }

        List<List<Action>> animations = new ArrayList<>();
        ComputerCombatGame game = (ComputerCombatGame) Gdx.app.getApplicationListener();
        if (newSoftware.size() > 0) {

            List<Action> cardAnimation = new ArrayList<>();
            Table t = new Table();
            t.setFillParent(true);
            Card loadNewCard = SQLAPI.getSingleton().getCardById(newSoftware.get(0).getID(), newSoftware.get(0).getOwnerUID());
            loadNewCard.setMatchID(newSoftware.get(0).getMatchID());
            CollectionCard card = new CollectionCard(game, screen.getSkin(), loadNewCard, 1);
            card.setTouchable(Touchable.disabled);
            Stage stage = screen.getMainStage();
            stage.addActor(t);
            card.setVisible(false);
            card.setPosition(stage.getWidth() / 2, -300);
            t.add(card).expand().center();
            Action cardAction = Actions.sequence(Actions.moveBy(0, -400), Actions.visible(true),
                    Actions.moveBy(0, 400, 1 * animationSpeed, Interpolation.fastSlow),
                    Actions.delay(1 * animationSpeed),
                    Actions.moveBy(0, -400, 1 * animationSpeed, Interpolation.slowFast));
            cardAction.setActor(card);
            cardAnimation.add(cardAction);
            animations.add(cardAnimation);

            List<SoftwareActor> softwareActors = screen.getSoftwareActors().get(currentPlayerUID);
            ComputerActor computerActor = screen.getComputerActors().get(currentPlayerUID);
            List<Action> softwareAnimation = new ArrayList<>();
            SoftwareActor softwareActor = new SoftwareActor(screen.getSkin(), !playerUID.equals(currentPlayerUID), loadNewCard, game);
            panel.clear();
            for (SoftwareActor s : softwareActors) {
                panel.add(s).row();
            }
            panel.add(softwareActor).row();
            panel.add(computerActor).expandY().growX().bottom();
            softwareActor.setVisible(false);
            softwareActors.add(softwareActor);
            Action softwareActorAction = Actions.sequence(
                    Actions.moveBy(0, -400),
                    Actions.visible(true),
                    Actions.moveBy(0, 400, 1 * animationSpeed, Interpolation.fastSlow));
            softwareActorAction.setActor(softwareActor);
            softwareAnimation.add(softwareActorAction);
            animations.add(softwareAnimation);
        }
        return animations;
    }

    @Override
    public void write(Json json) {
        json.writeValue("playerUID", playerUID);
        json.writeValue("newSoftware", newSoftware);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.newSoftware = json.readValue("newSoftware", List.class, jsonData);
    }
}
