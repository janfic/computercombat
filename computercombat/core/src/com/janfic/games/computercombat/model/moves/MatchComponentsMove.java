package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.animations.SwitchAnimation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class MatchComponentsMove extends Move implements Json.Serializable {

    private Component a, b;

    public MatchComponentsMove() {
    }

    public MatchComponentsMove(String playerUID, Component a, Component b) {
        super(playerUID);
        this.a = a;
        this.b = b;
    }

    @Override
    public List<MoveResult> doMove(MatchState originalState) {

        List<MoveResult> results = new ArrayList<>();

        MatchState newState = new MatchState(originalState);

        Component[][] board = newState.getComponentBoard();
        Component bb = board[b.getX()][b.getY()];
        Component ba = board[a.getX()][a.getY()];
        int oldBX = b.getX(), oldBY = b.getY();
        bb.setPosition(a.getX(), a.getY());
        ba.setPosition(oldBX, oldBY);
        board[ba.getX()][ba.getY()] = ba;
        board[bb.getX()][bb.getY()] = bb;

        //Switch and Create 1st MoveResult
        SwitchAnimation switchAnimation = new SwitchAnimation(a, b);
        List<MoveAnimation> animations = new ArrayList<>();
        animations.add(switchAnimation);
        MoveResult r = new MoveResult(this, originalState, newState, animations);
        results.add(r);

        originalState = newState;
        newState = new MatchState(newState);

        List<MoveResult> collectLoop = Move.collectComponentsCheck(newState, this);
        results.addAll(collectLoop);

        GameRules.isGameOver(results.get(results.size() - 1).getNewState());
        return results;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("playerUID", playerUID);
        json.writeValue("a", a);
        json.writeValue("b", b);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.playerUID = json.readValue("playerUID", String.class, jv);
        this.a = json.readValue("a", Component.class, jv);
        this.b = json.readValue("b", Component.class, jv);
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
            return (o.a.equals(this.a) && o.b.equals(this.b)) || (o.a.equals(this.b) && o.b.equals(this.a));
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Moved Components: [ " + a.getTextureName() + " , " + a.getX() + " , " + a.getY() + " , " + b.getTextureName() + " , " + b.getX() + " , " + b.getY() + " ]";
    }
}
