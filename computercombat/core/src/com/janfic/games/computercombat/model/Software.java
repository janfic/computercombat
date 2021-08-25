package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;

/**
 *
 * @author Jan Fic
 */
public abstract class Software extends Card {

    public Software(int level, int startingHealth, int startingArmor, int startingAttack, int startingMagic, Class<? extends Component>[] runComponents, int runRequirements, Ability ability) {
        super(level, startingHealth, startingArmor, startingAttack, startingMagic, runComponents, runRequirements, ability);
    }
    
}