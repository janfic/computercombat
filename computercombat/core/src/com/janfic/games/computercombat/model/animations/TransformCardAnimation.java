package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class TransformCardAnimation implements MoveAnimation {

    List<Card> oldCards, newCards;

    public TransformCardAnimation() {
    }

    public TransformCardAnimation(List<Card> oldCards, List<Card> newCards) {
        this.oldCards = oldCards;
        this.newCards = newCards;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> actions = new ArrayList<>();

        List<Action> transform = new ArrayList<>();
        for (Card oldCard : oldCards) {
            SoftwareActor actor = screen.getSoftwareActorByMatchID(oldCard.getOwnerUID(), oldCard.getMatchID());
            Action transformAction = Actions.sequence(
                    Actions.moveBy(-2.5f, 0.2f * animationSpeed),
                    Actions.repeat(10, Actions.sequence(Actions.moveBy(5, 0.2f * animationSpeed), Actions.moveBy(-5, 0.2f * animationSpeed))),
                    Actions.moveBy(2.5f, 0.2f * animationSpeed),
                    Actions.run(() -> {
                        actor.buildActor(!currentPlayerUID.equals(playerUID), (Software) newCards.get(oldCards.indexOf(oldCard)), screen.getGame());
                    })
            );
            transformAction.setActor(actor);
            transform.add(transformAction);
        }
        actions.add(transform);

        return actions;
    }

    @Override
    public void write(Json json) {
        json.writeValue("oldCards", this.oldCards);
        json.writeValue("newCards", this.newCards);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.newCards = json.readValue("newCards", List.class, jsonData);
        this.oldCards = json.readValue("oldCards", List.class, jsonData);
    }

}
