package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Computer;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.animations.ChangeStatAnim.ChangeStatAction;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class AttackAnimation implements MoveAnimation {

    String attackedUID, attackerUID;
    ObjectMap<Card, List<Card>> attacks;

    public AttackAnimation() {
    }

    public AttackAnimation(String attackerUID, String attackedUID, ObjectMap<Card, List<Card>> attacks) {
        this.attackedUID = attackedUID;
        this.attackerUID = attackerUID;
        this.attacks = attacks;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animations = new ArrayList<>();
        for (Entry<Card, List<Card>> entry : attacks.entries()) {
            List<Action> attackActions = new ArrayList<>();

            SoftwareActor attackerActor = screen.getSoftwareActorByMatchID(attackerUID, entry.key.getMatchID());
            attackerActor.setZIndex(1000);
            attackerActor.getParent().setZIndex(Integer.MAX_VALUE);
            for (Card attacked : entry.value) {
                if (attacked instanceof Software) {
                    SoftwareActor attackedActor = screen.getSoftwareActorByMatchID(attackedUID, attacked.getMatchID());
                    Vector2 posBack = attackerActor.localToStageCoordinates(new Vector2(attackerActor.getX(), attackerActor.getY()));
                    Vector2 pos = attackedActor.localToStageCoordinates(new Vector2(attackedActor.getX(), attackedActor.getY()));
                    posBack = attackerActor.stageToLocalCoordinates(posBack);
                    pos = attackerActor.stageToLocalCoordinates(pos);
                    Action attack = Actions.sequence(
                            Actions.moveTo(pos.x, pos.y, 0.5f * animationSpeed, Interpolation.exp5In),
                            Actions.moveTo(posBack.x, posBack.y, 0.5f * animationSpeed, Interpolation.exp5Out)
                    );
                    Action attackedAction;
                    Card a = attackedActor.getSoftware();
                    int armorDecrease = a.getArmor() > 0 ? Math.min(a.getArmor(), entry.key.getAttack()) : 0;
                    int healthDecrease = a.getHealth() <= entry.key.getAttack() - armorDecrease ? a.getHealth() : entry.key.getAttack() - armorDecrease;
                    attackedAction = Actions.sequence(
                            Actions.delay(0.5f * animationSpeed),
                            Actions.color(Color.RED),
                            Actions.color(Color.WHITE, 0.4f * animationSpeed),
                            new ChangeStatAction(0.5f * animationSpeed, "armor", -armorDecrease),
                            new ChangeStatAction(0.5f * animationSpeed, "health", -healthDecrease)
                    );
                    attackedAction.setActor(attackedActor);
                    attack.setActor(attackerActor);
                    attackActions.add(attack);
                    attackActions.add(attackedAction);
                } else if (attacked instanceof Computer) {
                    ComputerActor attackedActor = screen.getComputerActors().get(attackedUID);
                    Action attackedAction;
                    Vector2 posBack = attackerActor.localToStageCoordinates(new Vector2(attackerActor.getX(), attackerActor.getY()));
                    Vector2 pos = attackedActor.localToStageCoordinates(new Vector2(attackedActor.getX(), attackedActor.getY()));
                    posBack = attackerActor.stageToLocalCoordinates(posBack);
                    pos = attackerActor.stageToLocalCoordinates(pos);
                    Action attack = Actions.sequence(
                            Actions.moveTo(pos.x, pos.y, 0.5f * animationSpeed, Interpolation.exp5In),
                            Actions.moveTo(posBack.x, posBack.y, 0.5f * animationSpeed, Interpolation.exp5Out)
                    );
                    int healthDecrease = entry.key.getAttack();
                    attackedAction = Actions.sequence(
                            Actions.delay(0.5f * animationSpeed),
                            Actions.color(Color.RED),
                            Actions.color(Color.WHITE, 0.4f * animationSpeed),
                            new ChangeStatAction(0.5f * animationSpeed, "health", -healthDecrease)
                    );
                    attackedAction.setActor(attackedActor);
                    attack.setActor(attackerActor);
                    attackActions.add(attack);
                    attackActions.add(attackedAction);
                }
            }

            animations.add(attackActions);
        }

        return animations;
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("attacks", attacks, ObjectMap.class);
        json.writeValue("attackedUID", attackedUID, String.class);
        json.writeValue("attackerUID", attackerUID, String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.attacks = json.readValue("attacks", ObjectMap.class, jsonData);
        this.attackerUID = json.readValue("attackerUID", String.class, jsonData);
        this.attackedUID = json.readValue("attackedUID", String.class, jsonData);
    }

}
