package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.abilities.AttackAbility;
import com.janfic.games.computercombat.model.animations.CascadeAnimation;
import com.janfic.games.computercombat.model.animations.CollectAnimation;
import com.janfic.games.computercombat.model.components.BugComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public abstract class Move implements Json.Serializable {

    protected String playerUID;

    public Move() {
        this.playerUID = null;
    }

    public Move(String playerUID) {
        this.playerUID = playerUID;
    }

    public abstract List<MoveResult> doMove(MatchState state);

    public String getPlayerUID() {
        return playerUID;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.playerUID = json.readValue("playerUID", String.class, jsonData);
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("playerUID", playerUID);
    }

    public static List<MoveResult> collectComponentsCheck(MatchState state, Move move) {

        List<MoveResult> results = new ArrayList<>();

        MatchState newState = new MatchState(state);
        MatchState originalState = newState;
        newState = new MatchState(newState);

        //Collection check cycle
        boolean extraTurn = false;
        Map<Integer, List<Component>> collected = GameRules.getCurrentComponentMatches(newState.getComponentBoard());
        do {
            results.addAll(collectComponents(collected, originalState, newState, move));

            //Check for extraTurn
            List<Integer> marks = new ArrayList<>(collected.keySet());
            for (Integer mark : marks) {
                if (collected.get(mark).size() >= 4) {
                    extraTurn = true;
                }
            }

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

    public static List<MoveResult> collectComponents(Map<Integer, List<Component>> collected, MatchState originalState, MatchState newState, Move move) {
        Map<Component, Card> progress = new HashMap<>();
        CollectAnimation collectAnimation = new CollectAnimation(collected, progress);
        List<MoveAnimation> animation = new ArrayList<>();

        List<BugComponent> bugsCollected = new ArrayList<>();

        //Progress
        for (Component c : collectAnimation.getAllComponents()) {
            boolean collectedByCard = false;
            if (c instanceof BugComponent) {
                bugsCollected.add((BugComponent) c);
            }
            for (Card card : newState.activeEntities.get(originalState.currentPlayerMove.getUID())) {
                if (card.getRunProgress() < card.getRunRequirements()) {
                    for (Class<? extends Component> requirement : card.getRunComponents()) {
                        if (c.getClass().equals(requirement)) {
                            card.recieveComponents(requirement, 1);
                            collectedByCard = true;
                            progress.put(c, card);
                            break;
                        }
                    }
                }
                if (collectedByCard == true) {
                    break;
                }
            }
            if (collectedByCard == false) {
                newState.computers.get(originalState.currentPlayerMove.getUID()).addProgress(1);
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

        List<MoveResult> results = new ArrayList<>();

        CascadeAnimation cascadeAnimation = new CascadeAnimation(cascade);
        //Move
        animation.add(collectAnimation);
        animation.add(cascadeAnimation);

        // Finalize Results
        MoveResult moveResult = new MoveResult(move, originalState, newState, animation);
        results.add(moveResult);

        if (bugsCollected.isEmpty() == false) {
            ObjectMap<Card, List<Card>> attacks = new ObjectMap<>();

            if (newState.activeEntities.get(originalState.currentPlayerMove.getUID()).isEmpty() == false) {
                Card attacker = newState.activeEntities.get(originalState.currentPlayerMove.getUID()).get(0);
                List<Card> attacked = new ArrayList<>();
                if (newState.activeEntities.get(newState.getOtherProfile(originalState.currentPlayerMove).getUID()).isEmpty()) {
                    attacked.add(newState.computers.get(originalState.getOtherProfile(originalState.currentPlayerMove).getUID()));
                } else {
                    attacked.add(newState.activeEntities.get(newState.getOtherProfile(originalState.currentPlayerMove).getUID()).get(0));
                }
                attacks.put(attacker, attacked);
            }

            AttackAbility attackAbility = new AttackAbility(0, 0, attacks);
            List<MoveResult> attackResults = attackAbility.doAbility(newState, move);
            results.addAll(attackResults);
        }

        return results;
    }
}
