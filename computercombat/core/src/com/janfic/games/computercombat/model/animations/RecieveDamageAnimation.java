package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class RecieveDamageAnimation implements MoveAnimation {

    Card reciever;
    int damage;
    String playerUID;

    public RecieveDamageAnimation() {
        this.reciever = null;
        this.damage = 0;
        this.playerUID = null;
    }

    public RecieveDamageAnimation(Card recievingDamageCard, int damage, String playerUID) {
        this.reciever = recievingDamageCard;
        this.playerUID = playerUID;
        this.damage = damage;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen) {
        List<List<Action>> animations = new ArrayList<>();
        System.out.println("In AN?IMATION");
        List<Action> changeColorActions = new ArrayList<>();
        Action changeColor = Actions.sequence(Actions.color(Color.RED, 0.25f), Actions.color(Color.WHITE));
        for (SoftwareActor softwareActor : screen.getSoftwareActors().get(this.playerUID)) {
            if (softwareActor.getSoftware().equals(reciever)) {
                changeColor.setActor(softwareActor);
            }
        }

        if (screen.getComputerActors().get(this.playerUID).getComputer().equals(reciever)) {
            changeColor.setActor(screen.getComputerActors().get(this.playerUID));
        }

        changeColorActions.add(changeColor);
        animations.add(changeColorActions);
        return animations;
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("reciever", reciever, Card.class);
        json.writeValue("playerUID", playerUID, String.class);
        json.writeValue("damage", damage, Integer.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.reciever = json.readValue("reciever", Card.class, jsonData);
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.damage = json.readValue("damage", Integer.class, jsonData);
    }

}
