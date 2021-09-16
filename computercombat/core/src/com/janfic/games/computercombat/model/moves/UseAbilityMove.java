package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.MatchState;
import java.util.ArrayList;
import java.util.List;

public class UseAbilityMove extends Move {

    private Card entity, target;
    private int playerTargetUID, targetIndex;

    public UseAbilityMove() {
        super("");
    }

    public UseAbilityMove(String playerUID, Card entity, Card target, int targetIndex, int playerTarget) {
        super(playerUID);
        this.entity = entity;
        this.target = target;
        this.playerTargetUID = playerTarget;
        this.targetIndex = targetIndex;
    }

    @Override
    public List<MoveResult> doMove(MatchState state) {
        List<MoveResult> results = new ArrayList<>();
        //stubbed
        return results;
    }

    @Override
    public void write(Json json) {
        json.writeValue("player", playerUID);
        json.writeValue("entity", entity, Card.class);
        json.writeValue("target", target, Card.class);
        json.writeValue("playerTargetUID", playerTargetUID, Integer.class);
        json.writeValue("targetIndex", targetIndex, Integer.class);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.playerUID = json.readValue("player", String.class, jv);
        this.entity = json.readValue("entity", Card.class, jv);
        this.target = json.readValue("target", Card.class, jv);
        this.playerTargetUID = json.readValue("playerTargetUID", Integer.class, jv);
        this.targetIndex = json.readValue("targetIndex", Integer.class, jv);
    }
}