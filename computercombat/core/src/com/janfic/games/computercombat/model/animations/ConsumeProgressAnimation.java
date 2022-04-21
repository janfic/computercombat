package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
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
 * @author Jan Fic
 */
public class ConsumeProgressAnimation implements MoveAnimation {

    String playerUID;
    List<Card> software;

    public ConsumeProgressAnimation() {
    }

    public ConsumeProgressAnimation(String playerUID, List<Card> software) {
        this.software = software;
        this.playerUID = playerUID;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animation = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        int index = -1;
        List<SoftwareActor> softwareActors = screen.getSoftwareActors().get(this.playerUID);
        for (int i = 0; i < softwareActors.size(); i++) {
            SoftwareActor softwareActor = softwareActors.get(i);
            if (software.get(0).equals(softwareActor.getSoftware())) {
                index = i;
                break;
            }
        }

        if (software.get(0).equals(screen.getComputerActors().get(this.playerUID).getComputer())) {
            DrainProgressAction drain = new DrainProgressAction(1 * animationSpeed);
            drain.setActor(screen.getComputerActors().get(this.playerUID));
            actions.add(drain);
        } else if (index != -1) {
            DrainProgressAction drain = new DrainProgressAction(1 * animationSpeed);
            drain.setActor(softwareActors.get(index));
            actions.add(drain);
        }
        animation.add(actions);
        return animation;
    }

    @Override
    public void write(Json json) {
        json.writeValue("playerUID", playerUID, String.class);
        json.writeValue("software", software, List.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.software = json.readValue("software", List.class, Card.class, jsonData);
    }

    public class DrainProgressAction extends TemporalAction {

        public DrainProgressAction(float duration) {
            setDuration(duration);
        }

        @Override
        protected void update(float percent) {
            if (getActor() instanceof SoftwareActor) {
                SoftwareActor softwareActor = (SoftwareActor) getActor();
                float p = softwareActor.getSoftware().getRunRequirements() - (float) softwareActor.getSoftware().getRunRequirements() * percent;
                softwareActor.setProgress(p);
                softwareActor.getSoftware().setProgress((int) p);
            } else if (getActor() instanceof ComputerActor) {
                ComputerActor computerActor = (ComputerActor) getActor();
                computerActor.setProgress(20 - (20f * percent));
                computerActor.getComputer().setProgress((int) (20 - (20f * percent)));
            }
        }
    }
}
