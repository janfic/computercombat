package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author janfc
 */
public class ChangeStatAnim implements MoveAnimation {

    public final static String[] stats = new String[]{"health", "armor", "attack", "progress"};

    String statChange;
    int newAmount;
    Card card;
    String playerUID;

    public ChangeStatAnim() {
    }

    public ChangeStatAnim(String statsToChange, int newAmount, Card card, String playerUID) {
        this.statChange = statsToChange;
        this.newAmount = newAmount;
        this.card = card;
        this.playerUID = playerUID;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animation = new ArrayList<>();

        List<Action> actions = new ArrayList<>();
        if (card instanceof Card) {
            SoftwareActor softwareActor = screen.getSoftwareActorByMatchID(this.playerUID, card.getMatchID());
            Action action = new ChangeStatAction(1 * animationSpeed, statChange, newAmount);
            action.setActor(softwareActor);
            actions.add(action);
        }

        animation.add(actions);
        return animation;
    }

    @Override
    public void write(Json json) {
        json.writeValue("playerUID", playerUID, String.class);
        json.writeValue("card", card, Card.class);
        json.writeValue("statChange", statChange, String.class);
        json.writeValue("newAmount", newAmount, int.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.card = json.readValue("card", Card.class, jsonData);
        this.statChange = json.readValue("statChange", String.class, jsonData);
        this.newAmount = json.readValue("newAmount", int.class, jsonData);
    }

    public static class ChangeStatAction extends TemporalAction {

        String stat;
        int oldAttack, oldHealth, oldProgress, oldDefense;
        int amount;

        public ChangeStatAction(float duration, String stat, int amount) {
            setDuration(duration);
            this.stat = stat;
            this.amount = amount;
        }

        @Override
        public void setActor(Actor actor) {
            super.setActor(actor);
            if (actor instanceof SoftwareActor) {
                SoftwareActor softwareActor = (SoftwareActor) getActor();
                this.oldAttack = softwareActor.getSoftware().getAttack();
                this.oldHealth = softwareActor.getSoftware().getHealth();
                this.oldProgress = softwareActor.getSoftware().getRunProgress();
                this.oldDefense = softwareActor.getSoftware().getArmor();
            } else if (actor instanceof ComputerActor) {
                ComputerActor computerActor = (ComputerActor) getActor();
                this.oldAttack = computerActor.getComputer().getAttack();
                this.oldHealth = computerActor.getComputer().getHealth();
                this.oldProgress = computerActor.getComputer().getRunProgress();
                this.oldDefense = computerActor.getComputer().getArmor();
            }
        }

        @Override
        protected void update(float percent) {
            if (getActor() instanceof SoftwareActor) {
                SoftwareActor softwareActor = (SoftwareActor) getActor();
                float delta = (amount * percent);;
                switch (stat) {
                    case "health":
                        softwareActor.getSoftware().setHealth((int) (oldHealth + delta));
                        softwareActor.setHealth((int) (oldHealth + delta));
                        break;
                    case "attack":
                        softwareActor.getSoftware().setAttack((int) (oldAttack + delta));
                        softwareActor.setAttack((int) (oldAttack + delta));
                        break;
                    case "armor":
                        softwareActor.getSoftware().setArmor((int) (oldDefense + delta));
                        softwareActor.setArmor((int) (oldDefense + delta));
                        break;
                    case "progress":
                        softwareActor.getSoftware().setProgress((int) (oldProgress + delta));
                        softwareActor.setProgress((int) (oldProgress + delta));
                        break;
                }

            } else if (getActor() instanceof ComputerActor) {
                ComputerActor computerActor = (ComputerActor) getActor();
                float delta = (amount * percent);;
                switch (stat) {
                    case "health":
                        computerActor.getComputer().setHealth((int) (oldHealth + delta));
                        computerActor.setHealth((int) (oldHealth + delta));
                        break;
                    case "progress":
                        computerActor.getComputer().setProgress((int) (oldProgress + delta));
                        computerActor.setProgress((int) (oldProgress + delta));
                        break;
                }
            }
        }
    }

}
