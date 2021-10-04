package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Json.Serializable;
import com.janfic.games.computercombat.actors.Board;
import com.janfic.games.computercombat.actors.ComputerActor;
import com.janfic.games.computercombat.actors.SoftwareActor;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public interface MoveAnimation extends Serializable {

    public List<List<Action>> animate(String currentPlayerUID, String playerUID, Board board, Map<String, List<SoftwareActor>> softwareActors, Map<String, ComputerActor> computerActors);
}
