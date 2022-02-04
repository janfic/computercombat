package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.util.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class MultiAbility extends Ability {

    List<Ability> abilities;

    public MultiAbility() {
        super(new ArrayList<>());
    }

    public MultiAbility(List<Ability> abilities) {
        super(new ArrayList<>());
        for (Ability ability : abilities) {
            this.getSelectFilters().addAll(ability.getSelectFilters());
        }
        this.abilities = abilities;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MatchState lastState = new MatchState(state);
        for (int i = 0; i < abilities.size(); i++) {
            Ability ability = abilities.get(i);
            List<MoveResult> abilityResults = ability.doAbility(lastState, move);
            if (i != 0) {
                for (MoveResult abilityResult : abilityResults) {
                    int index = -1;
                    for (MoveAnimation animation : abilityResult.getAnimations()) {
                        if (animation instanceof ConsumeProgressAnimation) {
                            index = abilityResult.getAnimations().indexOf(animation);
                            break;
                        }
                    }
                    if (index >= 0) {
                        abilityResult.getAnimations().remove(index);
                    }
                }
            }
            results.addAll(abilityResults);
            lastState = results.get(results.size() - 1).getNewState();
            if (i < abilities.size() - 1) {
                if (lastState.currentPlayerMove.getUID() != move.getPlayerUID()) {
                    lastState.currentPlayerMove = state.getOtherProfile(lastState.currentPlayerMove);
                }
            }
        }

        return results;
    }
}
