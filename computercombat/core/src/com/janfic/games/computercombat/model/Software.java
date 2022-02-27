package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import java.util.Arrays;

/**
 *
 * @author Jan Fic
 */
public class Software extends Card {

    public Software(int id, String ownerUID, String name, Collection collection, String textureName, int level, int startingHealth, int startingArmor, int startingAttack, int startingMagic, int[] runComponents, int runRequirements, Ability ability, int rarity) {
        super(id, ownerUID, name, collection, textureName, level, startingHealth, startingArmor, startingAttack, startingMagic, runComponents, runRequirements, ability, rarity);
    }

    public Software() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        int[] runComponents = Arrays.copyOf(this.runComponents, this.runComponents.length);
        Software s = new Software(this.id, this.ownerUID, this.name, this.collection, this.textureName, this.level, this.maxHealth, this.maxArmor, this.maxAttack, this.magic, runComponents, this.runRequirements, this.ability, this.rarity);
        s.matchID = this.matchID;
        s.health = this.health;
        s.armor = this.armor;
        s.runProgress = this.runProgress;
        s.attack = this.attack;
        return s;
    }
}
