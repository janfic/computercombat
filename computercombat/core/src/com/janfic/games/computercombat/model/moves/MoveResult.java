package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.match.MatchState;
import java.util.List;

public class MoveResult {

    private final MatchState recordedState;
    private final Move move;
    private final List<MoveAnimation> animations;

    public MoveResult(Move move, MatchState recordedState, List<MoveAnimation> animations) {
        this.move = move;
        this.animations = animations;
        this.recordedState = recordedState;
    }

    public MatchState getState() {
        return recordedState;
    }

    public Move getMove() {
        return move;
    }

    public List<MoveAnimation> getAnimations() {
        return animations;
    }

    public MoveResult() {
        this.move = null;
        this.animations = null;
        this.recordedState = null;
    }
}
