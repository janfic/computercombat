package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

/**
 *
 * @author Jan Fic
 */
public abstract class Move implements Serializable {

    protected int playerUID;

    public Move(int playerUID) {
        this.playerUID = playerUID;
    }

    public abstract MatchState doMove(MatchState state);

    public int getPlayerUID() {
        return playerUID;
    }

    public class MatchComponentsMove extends Move {

        private Component a, b;

        public MatchComponentsMove(int playerUID, Component a, Component b) {
            super(playerUID);
            this.a = a;
            this.b = b;
        }

        @Override
        public MatchState doMove(MatchState state) {
            if (a.getX() == b.getX() && Math.abs(a.getY() - b.getY()) == 1) {

            }
            if (a.getY() == b.getY() && Math.abs(a.getX() - b.getX()) == 1) {

            }
            return null;
        }

        @Override
        public void write(Json json) {
            json.writeValue("player", playerUID);
            json.writeValue("a", a);
            json.writeValue("b", b);
        }

        @Override
        public void read(Json json, JsonValue jv) {
            this.a = json.readValue("a", Component.class, jv);
            this.a = json.readValue("b", Component.class, jv);
        }

    }

    public class UseAbilityMove extends Move {

        private Card entity, target;
        private int playerTargetUID, targetIndex;

        public UseAbilityMove() {
            super(0);
        }

        public UseAbilityMove(int playerUID, Card entity, Card target, int targetIndex, int playerTarget) {
            super(playerUID);
            this.entity = entity;
            this.target = target;
            this.playerTargetUID = playerTarget;
            this.targetIndex = targetIndex;
        }

        @Override
        public MatchState doMove(MatchState state) {
            //stubbed
            return null;
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
            this.playerUID = json.readValue("player", Integer.class, jv);
            this.entity = json.readValue("entity", Card.class, jv);
            this.target = json.readValue("target", Card.class, jv);
            this.playerTargetUID = json.readValue("playerTargetUID", Integer.class, jv);
            this.targetIndex = json.readValue("targetIndex", Integer.class, jv);
        }
    }
}
