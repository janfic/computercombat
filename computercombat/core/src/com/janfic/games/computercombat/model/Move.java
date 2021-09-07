package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public abstract class Move implements Serializable {

    protected String playerUID;

    public Move(String playerUID) {
        this.playerUID = playerUID;
    }

    public abstract MatchState doMove(MatchState state);

    public String getPlayerUID() {
        return playerUID;
    }

    public static class MatchComponentsMove extends Move {

        private Component a, b;

        public MatchComponentsMove(String playerUID, Component a, Component b) {
            super(playerUID);
            this.a = a;
            this.b = b;
        }

        @Override
        public MatchState doMove(MatchState state) {
            MatchState copy = new MatchState(state);
            Component[][] board = copy.getComponentBoard();
            board[b.getX()][b.getY()] = a;
            board[a.getX()][a.getY()] = b;
            return copy;
        }

        @Override
        public void write(Json json) {
            json.writeValue("playerUID", playerUID);
            json.writeValue("a", a);
            json.writeValue("b", b);
        }

        @Override
        public void read(Json json, JsonValue jv) {
            this.playerUID = json.readValue("playerUID", String.class, jv);
            this.a = json.readValue("a", Component.class, jv);
            this.a = json.readValue("b", Component.class, jv);
        }

        public Component getA() {
            return a;
        }

        public Component getB() {
            return b;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MatchComponentsMove) {
                MatchComponentsMove o = (MatchComponentsMove) obj;
                return o.hashCode() == this.hashCode();
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.a);
            hash = 53 * hash + Objects.hashCode(this.b);
            return hash;
        }
    }

    public static class UseAbilityMove extends Move {

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
            this.playerUID = json.readValue("player", String.class, jv);
            this.entity = json.readValue("entity", Card.class, jv);
            this.target = json.readValue("target", Card.class, jv);
            this.playerTargetUID = json.readValue("playerTargetUID", Integer.class, jv);
            this.targetIndex = json.readValue("targetIndex", Integer.class, jv);
        }
    }
}
