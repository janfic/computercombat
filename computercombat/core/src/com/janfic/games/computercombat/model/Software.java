package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;

/**
 *
 * @author Jan Fic
 */
public class Software extends Card {
    
    public Software(String name, String pack, String textureName, int level, int startingHealth, int startingArmor, int startingAttack, int startingMagic, Class<? extends Component>[] runComponents, int runRequirements, Ability ability) {
        super(name, pack, textureName, level, startingHealth, startingArmor, startingAttack, startingMagic, runComponents, runRequirements, ability);
    }
    
    public static Software createFromJson(String data) {
        Json json = new Json();
        return json.fromJson(Software.class, data);
    }
}
