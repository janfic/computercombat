package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.ChangeStatAnim.ChangeStatAction;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class ReceiveDamageAnimation implements MoveAnimation {

    Card reciever;
    int damage;
    String playerUID;

    public ReceiveDamageAnimation() {
        this.reciever = null;
        this.damage = 0;
        this.playerUID = null;
    }

    public ReceiveDamageAnimation(Card recievingDamageCard, int damage, String playerUID) {
        this.reciever = recievingDamageCard;
        this.playerUID = playerUID;
        this.damage = damage;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animations = new ArrayList<>();
        List<Action> changeColorActions = new ArrayList<>();

        SoftwareActor softwareActor = screen.getSoftwareActorByMatchID(reciever.getOwnerUID(), reciever.getMatchID());
        int armorDecrease = reciever.getArmor() > 0 ? Math.min(reciever.getArmor(), damage) : 0;
        int healthDecrease = reciever.getHealth() <= damage - armorDecrease ? reciever.getHealth() : damage - armorDecrease;
        Action attackedAction = Actions.sequence(
                Actions.delay(0.5f * animationSpeed),
                Actions.color(Color.RED),
                Actions.color(Color.WHITE, 0.4f * animationSpeed),
                new ChangeStatAction(0.5f * animationSpeed, "armor", -armorDecrease),
                new ChangeStatAction(0.5f * animationSpeed, "health", -healthDecrease)
        );
        attackedAction.setActor(softwareActor);
        changeColorActions.add(attackedAction);

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
