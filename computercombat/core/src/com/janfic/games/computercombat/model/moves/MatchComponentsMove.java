package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.animations.CascadeAnimation;
import com.janfic.games.computercombat.model.animations.CollectAnimation;
import com.janfic.games.computercombat.model.animations.SwitchAnimation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //Collection check cycle
        boolean extraTurn = false;
        Map<Integer, List<Component>> collected = GameRules.getCurrentComponentMatches(newState.getComponentBoard());
        do {

            List<MoveAnimation> animation = new ArrayList<>();

            Map<Component, Card> progress = new HashMap<>();
            CollectAnimation collectAnimation = new CollectAnimation(collected, progress);
            //Progress
            for (Component c : collectAnimation.getAllComponents()) {
                boolean collectedByCard = false;
                System.out.println(originalState.currentPlayerMove);
                System.out.println(newState.activeEntities);
                System.out.println(newState.activeEntities.get(originalState.currentPlayerMove.getUID()));
                for (Card card : newState.activeEntities.get(originalState.currentPlayerMove.getUID())) {
                    if (card.getRunProgress() < card.getRunRequirements()) {
                        for (Class<? extends Component> requirement : card.getRunComponents()) {
                            if (c.getClass().equals(requirement)) {
                                card.recieveComponents(requirement, 1);
                                collectedByCard = true;
                                break;
                            }
                        }
                    }
                }
                if (collectedByCard == false) {
                    newState.computers.get(originalState.currentPlayerMove.getUID()).addProgress(1);
                    System.out.println(c.toString());
                }
            }

            //Collect
            for (Component component : collectAnimation.getAllComponents()) {
                newState.getComponentBoard()[component.getX()][component.getY()] = null;
            }

            //Cascade
            List<CascadeAnimation.CascadeData> cascade = new ArrayList<>();
            for (int x = 0; x < newState.componentBoard.length; x++) {
                for (int y = newState.componentBoard[x].length - 1; y >= 0; y--) {
                    if (newState.componentBoard[x][y] == null) {
                        Component above = null;
                        for (int fy = y - 1; fy >= 0; fy--) {
                            if (newState.componentBoard[x][fy] != null) {
                                above = newState.componentBoard[x][fy];
                                newState.componentBoard[x][fy] = null;
                                break;
                            }
                        }
                        if (above != null) {
                            int px = above.getX(), py = above.getY();
                            above.setPosition(x, y);
                            cascade.add(new CascadeAnimation.CascadeData(above, px, py));
                        }
                        newState.componentBoard[x][y] = above;
                    }
                }
            }

            //Spawn
            List<Component> newComponents = GameRules.getNewComponents(collectAnimation.getAllComponents().size());
            int n = 0;
            for (int x = 0; x < newState.componentBoard.length; x++) {
                for (int y = newState.componentBoard[x].length - 1; y >= 0; y--) {
                    if (newState.componentBoard[x][y] == null) {
                        newState.componentBoard[x][y] = newComponents.get(n);
                        newComponents.get(n).setPosition(x, y);
                        cascade.add(new CascadeAnimation.CascadeData(newComponents.get(n), x, (-y) - 1));
                        n++;
                    }
                }
            }

            CascadeAnimation cascadeAnimation = new CascadeAnimation(cascade);
            //Move
            animation.add(collectAnimation);
            animation.add(cascadeAnimation);

            //Check for extraTurn
            List<Integer> marks = new ArrayList<>(collected.keySet());
            for (Integer mark : marks) {
                if (collected.get(mark).size() >= 4) {
                    extraTurn = true;
                }
            }

            // Finalize Results
            MoveResult moveResult = new MoveResult(this, originalState, newState, animation);
            results.add(moveResult);

            //Prepare for next result
            originalState = newState;
            newState = new MatchState(newState);
            collected = GameRules.getCurrentComponentMatches(newState.getComponentBoard());
        } while (!collected.isEmpty());

        if (extraTurn == false) {
            MoveResult last = results.get(results.size() - 1);
            last.getNewState().currentPlayerMove = last.getNewState().getOtherProfile(last.getNewState().currentPlayerMove);
        }

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
