package com.janfic.games.computercombat.model.animations;

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
public class DestroyCardAnimation implements MoveAnimation {

    String playerUID;
    List<Card> destroyed;

    public DestroyCardAnimation() {
    }

    public DestroyCardAnimation(String playerUID, List<Card> destroyed) {
        this.playerUID = playerUID;
        this.destroyed = destroyed;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen) {
        List<List<Action>> animation = new ArrayList<>();

        List<Action> actions = new ArrayList<>();
        for (Card card : destroyed) {
            SoftwareActor actor = screen.getSoftwareActorByMatchID(this.playerUID, card.getMatchID());

            Action action = Actions.sequence(Actions.fadeOut(1), Actions.removeActor(), Actions.run(new Runnable() {
                @Override
                public void run() {
                    screen.getSoftwareActors().get(DestroyCardAnimation.this.playerUID).remove(actor);
                    screen.buildPanels();
                }
            }));
            action.setActor(actor);
            actions.add(action);

        }
        animation.add(actions);

        return animation;
    }

    @Override
    public void write(Json json) {
        json.writeValue("destroyed", destroyed, List.class);
        json.writeValue("playerUID", playerUID, String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
        this.destroyed = json.readValue("destroyed", List.class, jsonData);
    }

}
