package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.animations.ChangeStatAnim.ChangeStatAction;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class AttackAnimation implements MoveAnimation {

    String attackedUID, attackerUID;
    Map<String, Array<Integer>> attacks;

    public AttackAnimation() {
    }

    public AttackAnimation(String attackerUID, String attackedUID, Map<String, Array<Integer>> attacks) {
        this.attackedUID = attackedUID;
        this.attackerUID = attackerUID;
        this.attacks = attacks;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animations = new ArrayList<>();
        for (String key : attacks.keySet()) {
            List<Action> attackActions = new ArrayList<>();

            System.out.println(" HERE " + key);
            SoftwareActor attackerActor = screen.getSoftwareActorByMatchID(attackerUID, Integer.parseInt(key));
            attackerActor.setZIndex(1000);
            attackerActor.getParent().setZIndex(Integer.MAX_VALUE);
            for (Integer matchID : attacks.get(key)) {
                Card attacker = attackerActor.getSoftware();
                Card attacked = screen.getSoftwareActorByMatchID(attackedUID, matchID).getSoftware();
                if (attacked.getID() > 0) {
                    SoftwareActor attackedActor = screen.getSoftwareActorByMatchID(attackedUID, matchID);
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
                    int armorDecrease = a.getArmor() > 0 ? Math.min(a.getArmor(), attacker.getAttack()) : 0;
                    int healthDecrease = a.getHealth() <= attacker.getAttack() - armorDecrease ? a.getHealth() : attacker.getAttack() - armorDecrease;
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
                } else if (attacked.getID() == 0) {
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
                    int healthDecrease = attacker.getAttack();
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
        json.writeValue("attacks", attacks, Map.class);
        json.writeValue("attackedUID", attackedUID, String.class);
        json.writeValue("attackerUID", attackerUID, String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.attacks = json.readValue("attacks", Map.class, jsonData);
        this.attackerUID = json.readValue("attackerUID", String.class, jsonData);
        this.attackedUID = json.readValue("attackedUID", String.class, jsonData);
    }

}
