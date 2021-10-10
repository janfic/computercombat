package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public abstract class Card implements Json.Serializable {

    protected int id;
    protected int health, armor, attack, magic;
    protected int level;
    protected int maxHealth, maxArmor, maxAttack;
    protected Ability ability;
    protected Class<? extends Component>[] runComponents;
    protected int runProgress, runRequirements;
    protected Trait[] traits;
    protected int traitsUnlocked;
    protected String name, pack, textureName;

    public Card() {
        this(0, "CARD", "Computer", "Default", 1, 0, 0, 0, 0, new Class[]{}, 0, null);
    }

    public Card(int id, String name, String pack, String textureName, int level, int startingHealth, int startingArmor, int startingAttack, int startingMagic, Class<? extends Component>[] runComponents, int runRequirements, Ability ability) {
        this.name = name;
        this.pack = pack;
        this.textureName = textureName;
        this.health = startingHealth + ((3 + level - 1) / 4);
        this.armor = startingArmor + ((2 + level - 1) / 4);
        this.attack = startingAttack + ((1 + level - 1) / 4);
        this.magic = startingMagic + ((level - 1) / 4);
        this.maxHealth = health;
        this.maxArmor = armor;
        this.maxAttack = attack;
        this.ability = ability;
        this.runComponents = runComponents;
        this.runRequirements = runRequirements;
        this.level = level;
        this.runProgress = 0;
        this.traitsUnlocked = 0;
        this.id = id;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void beginMatch(MatchState state) {
        for (Trait trait : traits) {
            trait.beginMatch(state);
        }
    }

    /**
     * Called every time it is a player's new turn
     *
     * @param state
     */
    public void newTurn(MatchState state) {
        for (Trait trait : traits) {
            trait.newTurn(state);
        }
    }

    /**
     *
     * @return health
     */
    public int getHealth() {
        return health;
    }

    /**
     *
     * @return armor
     */
    public int getArmor() {
        return armor;
    }

    /**
     *
     * @return attack
     */
    public int getAttack() {
        return attack;
    }

    /**
     *
     * @return magic
     */
    public int getMagic() {
        return magic;
    }

    public int getLevel() {
        return level;
    }

    public Ability getAbility() {
        return this.ability;
    }

    public int getMaxArmor() {
        return maxArmor;
    }

    public int getMaxAttack() {
        return maxAttack;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Called when software receives health
     *
     * @param health
     * @return actual health received
     */
    public int recieveHealth(int health) {
        this.health += health;
        return health;
    }

    /**
     * Called when this software receives damage.
     *
     * @param damage
     * @return actual damage received
     */
    public int recieveDamage(int damage) {
        if (armor > 0) {
            if (damage >= armor) {
                damage = damage - armor;
                armor = 0;
            } else {
                armor -= damage;
                damage = 0;
            }
        }
        this.health -= damage;
        return damage;
    }

    /**
     * Called when this software receives armor
     *
     * @param armor
     * @return amount armor is actually changed by
     */
    public int changeArmor(int armor) {
        this.armor += armor;
        return armor;
    }

    /**
     * Called when this software receives or reduces magic.
     *
     * @param magic
     * @return amount magic is actually changed
     */
    public int changeMagic(int magic) {
        this.magic += magic;
        return magic;
    }

    /**
     * Called when this software has its attack changed.
     *
     * @param attack
     * @return amount attack is actually changed
     */
    public int changeAttack(int attack) {
        this.attack += attack;
        return attack;
    }

    /**
     *
     * @param type
     * @param amount
     * @return amount of components left over
     */
    public int recieveComponents(Class<? extends Component> type, int amount) {
        for (Class<? extends Component> t : runComponents) {
            if (type == t) {
                if (runProgress < runRequirements) {
                    runProgress += amount;
                    amount = 0;
                    if (runProgress > runRequirements) {
                        amount += runProgress - runRequirements;
                    }
                }
                return amount;
            }
        }
        return amount;
    }

    public void recieveProgress(int amount) {
        runProgress += amount;
        if (runProgress > runRequirements) {
            runProgress = runRequirements;
        }
    }

    public Class<? extends Component>[] getRunComponents() {
        return runComponents;
    }

    public int getRunRequirements() {
        return runRequirements;
    }

    public int getRunProgress() {
        return runProgress;
    }

    public String getPack() {
        return pack;
    }

    public String getTextureName() {
        return textureName;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", this.name);
        json.writeValue("id", this.id);
        json.writeValue("pack", this.pack);
        json.writeValue("textureName", this.textureName);
        json.writeValue("health", this.health);
        json.writeValue("armor", this.armor);
        json.writeValue("attack", this.attack);
        json.writeValue("level", this.level);
        json.writeValue("magic", this.magic);
        json.writeValue("maxHealth", this.maxHealth);
        json.writeValue("maxArmor", this.maxArmor);
        json.writeValue("runProgress", this.runProgress);
        json.writeValue("runRequirements", this.runRequirements);
        json.writeArrayStart("runComponents");
        for (Class<? extends Component> runComponent : runComponents) {
            json.writeValue((Object) runComponent.getName(), String.class);
        }
        json.writeArrayEnd();
        json.writeValue("ability", ability);

    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.name = json.readValue("name", String.class, jv);
        this.id = json.readValue("id", Integer.class, jv);
        this.pack = json.readValue("pack", String.class, jv);
        this.textureName = json.readValue("textureName", String.class, jv);
        this.health = json.readValue("health", Integer.class, jv);
        this.armor = json.readValue("armor", Integer.class, jv);
        this.attack = json.readValue("attack", Integer.class, jv);
        this.level = json.readValue("level", Integer.class, jv);
        this.maxHealth = json.readValue("maxHealth", Integer.class, jv);
        this.maxArmor = json.readValue("maxArmor", Integer.class, jv);
        this.runProgress = json.readValue("runProgress", Integer.class, jv);
        this.runRequirements = json.readValue("runRequirements", Integer.class, jv);
        String[] components = jv.get("runComponents").asStringArray();
        List<Class<? extends Component>> foundComponents = new ArrayList<>();
        for (String component : components) {
            try {
                Class c = Class.forName(component);
                foundComponents.add(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.runComponents = foundComponents.toArray(this.runComponents);
        this.ability = json.readValue("ability", Ability.class, jv);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Card || obj instanceof Software) {
            return ((Card) obj).hashCode() == this.hashCode();
        }
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.pack);
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
