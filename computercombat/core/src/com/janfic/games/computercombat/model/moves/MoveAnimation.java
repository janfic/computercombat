package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public interface MoveAnimation {

    public List<List<Action>> animate(List<ComponentActor> componentActors, List<SoftwareActor> softwareActors);
}
