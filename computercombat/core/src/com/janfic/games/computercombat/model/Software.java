package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;

/**
 *
 * @author Jan Fic
 */
public class Software extends Card {

    public Software(int id, String ownerUID, String name, Collection collection, String textureName, int level, int startingHealth, int startingArmor, int startingAttack, int startingMagic, Class<? extends Component>[] runComponents, int runRequirements, Ability ability, int rarity) {
        super(id, ownerUID, name, collection, textureName, level, startingHealth, startingArmor, startingAttack, startingMagic, runComponents, runRequirements, ability, rarity);
    }

    public Software() {
    }
}
