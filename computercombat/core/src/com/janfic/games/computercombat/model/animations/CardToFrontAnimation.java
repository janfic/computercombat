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
public class CardToFrontAnimation implements MoveAnimation {

    List<Card> cards;

    public CardToFrontAnimation() {
    }

    public CardToFrontAnimation(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> animation = new ArrayList<>();
        List<Action> toFrontActions = new ArrayList<>();

        for (Card card : cards) {
            SoftwareActor first = screen.getSoftwareActors().get(card.getOwnerUID()).get(0);
            SoftwareActor actor = screen.getSoftwareActorByMatchID(card.getOwnerUID(), card.getMatchID());
            screen.getSoftwareActors().get(card.getOwnerUID()).remove(actor);
            screen.getSoftwareActors().get(card.getOwnerUID()).add(0, actor);
            Action toFrontAction = Actions.sequence(
                    Actions.moveTo(first.getX(), first.getY(), 1 * animationSpeed),
                    Actions.run(() -> {
                        screen.buildPanels();
                    })
            );
            toFrontAction.setActor(actor);
            toFrontActions.add(toFrontAction);
        }

        animation.add(toFrontActions);
        return animation;
    }

    @Override
    public void write(Json json) {
        json.writeValue("cards", cards);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.cards = json.readValue("Cards", List.class, jsonData);
    }

}
