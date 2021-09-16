package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json.Serializable;
import com.janfic.games.computercombat.actors.ComponentActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public interface MoveAnimation extends Serializable {

    public List<List<Action>> animate(List<ComponentActor> componentActors, List<SoftwareActor> softwareActors);
}
