package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.screens.MatchScreen;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class SwitchAnimation implements MoveAnimation {

    private Component a, b;

    public SwitchAnimation(Component a, Component b) {
        this.a = a;
        this.b = b;
    }

    private SwitchAnimation() {
        this.a = null;
        this.b = null;
    }

    @Override
    public List<List<Action>> animate(String currentPlayerUID, String playerUID, MatchScreen screen, float animationSpeed) {
        List<List<Action>> actions = new ArrayList<>();

        if (currentPlayerUID.equals(playerUID)) {
            return actions;
        }

        ComponentActor aActor = screen.getBoard().getBoard()[a.getX()][a.getY()].getActor(), bActor = screen.getBoard().getBoard()[b.getX()][b.getY()].getActor();
        if (aActor == null || bActor == null || aActor == bActor) {
            return actions;
        }

        List<Action> switchActions = new ArrayList<>();
        Vector2 sA = new Vector2(aActor.getX(), aActor.getY());
        Vector2 sB = new Vector2(bActor.getX(), bActor.getY());
        Action actionA = Actions.sequence(
                Actions.rotateTo(0),
                Actions.moveTo(sA.x, sA.y, 0.35f * animationSpeed),
                Actions.scaleTo(1, 1, 0.25f * animationSpeed),
                Actions.rotateTo(0, 0.25f * animationSpeed)
        );
        Action actionB = Actions.sequence(
                Actions.rotateTo(0),
                Actions.moveTo(sB.x, sB.y, 0.35f * animationSpeed),
                Actions.scaleTo(1, 1, 0.25f * animationSpeed),
                Actions.rotateTo(0, 0.25f * animationSpeed)
        );
        actionA.setActor(bActor);
        actionB.setActor(aActor);
        switchActions.add(actionA);
        switchActions.add(actionB);
        System.out.println("ANIMATION");

        actions.add(switchActions);
        return actions;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("a", a);
        json.writeValue("b", b);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.a = json.readValue("a", Component.class, jv);
        this.b = json.readValue("b", Component.class, jv);
    }

}
