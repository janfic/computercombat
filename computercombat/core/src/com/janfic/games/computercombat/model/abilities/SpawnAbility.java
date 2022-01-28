package com.janfic.games.computercombat.model.abilities;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.animations.SpawnAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class SpawnAbility extends Ability {

    Class<? extends Component> componentType;
    int amount;

    public SpawnAbility() {
        super(new ArrayList<>());
    }

    public SpawnAbility(Class<? extends Component> componentType, int amount, List<Filter> selectFilters) {
        super(selectFilters);
        this.componentType = componentType;
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();
        UseAbilityMove useAbilityMove = (UseAbilityMove) move;
        MatchState newState = new MatchState(state);

        int index = newState.activeEntities.get(useAbilityMove.getPlayerUID()).indexOf(useAbilityMove.getCard());
        newState.activeEntities.get(useAbilityMove.getPlayerUID()).get(index).setProgress(0);

        List<int[]> newCoords = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int[] newLocation = new int[]{(int) (Math.random() * 8), (int) (Math.random() * 8)};
            boolean duplicate = false;
            while (newState.getComponentBoard()[newLocation[0]][newLocation[1]].getClass() == componentType || duplicate == true) {
                for (int[] newCoord : newCoords) {
                    if (newLocation[0] == newCoord[0] && newLocation[1] == newCoord[1]) {
                        duplicate = true;
                        break;
                    }
                }
                newLocation = new int[]{(int) (Math.random() * 8), (int) (Math.random() * 8)};
            }
            newCoords.add(newLocation);
        }

        List<Component> spawned = new ArrayList<>();
        List<Component> destroyed = new ArrayList<>();
        for (int[] newCoord : newCoords) {
            try {
                Component destroy = newState.getComponentBoard()[newCoord[0]][newCoord[1]];
                Component spawn = componentType.getConstructor(int.class, int.class).newInstance(newCoord[0], newCoord[1]);
                newState.getComponentBoard()[newCoord[0]][newCoord[1]] = spawn;

                spawned.add(spawn);
                destroyed.add(destroy);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        List<MoveAnimation> animations = new ArrayList<>();
        List<Card> used = new ArrayList<>();
        used.add(useAbilityMove.getCard());
        animations.add(new ConsumeProgressAnimation(useAbilityMove.getPlayerUID(), used));
        animations.add(new SpawnAnimation(destroyed, spawned));
        MoveResult moveResult = new MoveResult(move, state, newState, animations);
        List<MoveResult> afterMove = Move.collectComponentsCheck(newState, move);
        results.add(moveResult);
        results.addAll(afterMove);
        return results;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("amount", amount, Integer.class);
        json.writeValue("componentType", componentType.getName(), String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.amount = json.readValue("amount", Integer.class, jsonData);
        String type = json.readValue("componentType", String.class, jsonData);
        try {
            this.componentType = (Class<? extends Component>) Class.forName(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
