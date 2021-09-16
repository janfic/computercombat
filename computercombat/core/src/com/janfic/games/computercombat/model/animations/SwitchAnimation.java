package com.janfic.games.computercombat.model.animations;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class SwitchAnimation implements MoveAnimation {

    private final Component a, b;

    public SwitchAnimation(Component a, Component b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public List<List<Action>> animate(List<ComponentActor> componentActors, List<SoftwareActor> softwareActors) {
        List<List<Action>> actions = new ArrayList<>();
        return actions;
    }

}
