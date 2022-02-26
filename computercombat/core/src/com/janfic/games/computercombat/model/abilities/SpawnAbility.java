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

    int componentType;
    StateAnalyzer<Integer> amount;

    public SpawnAbility() {
        super(new ArrayList<>());
    }

    public SpawnAbility(Integer componentType, StateAnalyzer<Integer> amount, List<Filter> selectFilters) {
        super(selectFilters);
        this.componentType = componentType;
        this.amount = amount;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MoveAnimation consumeProgressAnimation = Ability.consumeCardProgress(state, move);

        List<int[]> newCoords = new ArrayList<>();
        int amountSpawned = amount.analyze(state, move);
        for (int i = 0; i < amountSpawned; i++) {
            int[] newLocation = new int[]{(int) (Math.random() * 8), (int) (Math.random() * 8)};
            boolean duplicate = false;
            while (state.getComponentBoard()[newLocation[0]][newLocation[1]].getColor() == componentType || duplicate == true) {
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
            Component destroy = state.getComponentBoard()[newCoord[0]][newCoord[1]];
            Component spawn = new Component(componentType, newCoord[0], newCoord[1]);
            state.getComponentBoard()[newCoord[0]][newCoord[1]].changeColor(spawn.getColor());
            state.getComponentBoard()[newCoord[0]][newCoord[1]].invalidate();
            state.getComponentBoard()[newCoord[0]][newCoord[1]].invalidateNeighbors();
            spawned.add(spawn);
            destroyed.add(destroy);
        }

        List<MoveAnimation> animations = new ArrayList<>();
        if (consumeProgressAnimation != null) {
            animations.add(consumeProgressAnimation);
        }
        animations.add(new SpawnAnimation(destroyed, spawned));
        MoveResult moveResult = new MoveResult(move, MatchState.record(state), animations);
        List<MoveResult> afterMove = state.results(move);
        results.add(moveResult);
        results.addAll(afterMove);
        return results;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("componentType", componentType, Integer.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.componentType = json.readValue("componentType", Integer.class, jsonData);
    }
}
