package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.*;
import com.janfic.games.computercombat.model.moves.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author janfc
 */
public class AttackAbility extends Ability {

    Map<Card, List<Card>> attacks;

    public AttackAbility() {
        super(0,0);
        this.attacks = null;
    }

    public AttackAbility(int selectedComponents, int selectedSoftware, Map<Card, List<Card>> attacks) {
        super(selectedComponents, selectedSoftware);
        this.attacks = attacks;
    }

    @Override
    public List<MoveResult> doAbility(MatchState state, Move move) {
        List<MoveResult> results = new ArrayList<>();

        MatchState newState = new MatchState(state);
        
        return results;
    }
}
