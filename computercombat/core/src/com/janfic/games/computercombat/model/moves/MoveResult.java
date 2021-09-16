package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.MatchState;
import java.util.List;

public class MoveResult {

    private final MatchState oldState, newState;
    private final Move move;
    private final List<MoveAnimation> animations;

    public MoveResult(Move move, MatchState oldState, MatchState newState, List<MoveAnimation> animations) {
        this.move = move;
        this.newState = newState;
        this.oldState = oldState;
        this.animations = animations;
    }

    public MatchState getNewState() {
        return newState;
    }

    public Move getMove() {
        return move;
    }

    public MatchState getOldState() {
        return oldState;
    }

    public List<MoveAnimation> getAnimations() {
        return animations;
    }
    
    public MoveResult() {
        this.oldState = null;
        this.newState = null;
        this.move = null;
        this.animations = null;
    }
}
