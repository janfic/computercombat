package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.match.MatchState;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public class Card implements Json.Serializable, Comparable<Card>, Cloneable {

    protected int id, matchID;
    protected int health, armor, attack, magic;
    protected int level;
    protected int maxHealth, maxArmor, maxAttack;
    protected Ability ability;
    protected int[] runComponents;
    protected int runProgress, runRequirements;
    protected Trait[] traits;
    protected int traitsUnlocked;
    protected String name, textureName;
    protected Collection collection;
    protected String ownerUID;
    protected int rarity;

    public Card() {
        this(0, "none", "CARD", new Collection(1, "Computer", "computer", "computer_pack", "computer_pack", 50), "Default", 1, 0, 0, 0, 0, new int[]{}, 0, null, 0);
    }

    public Card(int id, String ownerUID, String name, Collection collection, String textureName, int level, int startingHealth, int startingArmor, int startingAttack, int startingMagic, int[] runComponents, int runRequirements, Ability ability, int rarity) {
        this.name = name;
        this.collection = collection;
        this.textureName = textureName;
        this.ownerUID = ownerUID;
        this.health = startingHealth + ((3 + level - 1) / 4);
        this.armor = startingArmor + ((2 + level - 1) / 4);
        this.attack = startingAttack;
        this.magic = startingMagic + ((level - 1) / 4);
        this.maxHealth = health;
        this.maxArmor = armor;
        this.maxAttack = startingAttack;
        this.ability = ability;
        this.runComponents = runComponents;
        this.runRequirements = runRequirements;
        this.level = level;
        this.runProgress = 0;
        this.traitsUnlocked = 0;
        this.id = id;
        this.rarity = rarity;
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

    public int getRarity() {
        return rarity;
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

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public void generateMatchID() {
        this.matchID = (int) (Math.random() * Integer.MAX_VALUE);
    }

    /**
     * Called when this software receives damage.
     *
     * @param damage
     * @return actual damage received
     */
    public int recieveDamage(int damage) {
        if (armor > 0) {
            armor -= damage;
            if (armor < 0) {
                this.health += armor;
                this.armor = 0;
            }
        } else {
            this.health -= damage;
            if (health < 0) {
                health = 0;
            }
        }
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

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    /**
     *
     * @param type
     * @param amount
     * @return amount of components left over
     */
    public int recieveComponents(int type, int amount) {
        for (int t : runComponents) {
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

    public void setProgress(int runProgress) {
        this.runProgress = runProgress;
    }

    public int[] getRunComponents() {
        return runComponents;
    }

    public int getRunRequirements() {
        return runRequirements;
    }

    public int getRunProgress() {
        return runProgress;
    }

    public Collection getCollection() {
        return collection;
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

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("name", this.name);
        json.writeValue("id", this.id);
        json.writeValue("matchID", this.matchID);
        json.writeValue("collection", this.collection);
        json.writeValue("textureName", this.textureName);
        json.writeValue("health", this.health);
        json.writeValue("armor", this.armor);
        json.writeValue("attack", this.attack);
        json.writeValue("maxAttack", this.maxAttack);
        json.writeValue("level", this.level);
        json.writeValue("rarity", this.rarity);
        json.writeValue("magic", this.magic);
        json.writeValue("maxHealth", this.maxHealth);
        json.writeValue("maxArmor", this.maxArmor);
        json.writeValue("runProgress", this.runProgress);
        json.writeValue("runRequirements", this.runRequirements);
        json.writeValue("ownerUID", this.ownerUID);
        json.writeValue("runComponents", this.runComponents);
        json.writeValue("ability", ability);

    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.name = json.readValue("name", String.class, jv);
        this.id = json.readValue("id", Integer.class, jv);
        this.matchID = json.readValue("matchID", Integer.class, jv);
        this.collection = json.readValue("collection", Collection.class, jv);
        this.textureName = json.readValue("textureName", String.class, jv);
        this.health = json.readValue("health", Integer.class, jv);
        this.armor = json.readValue("armor", Integer.class, jv);
        this.attack = json.readValue("attack", Integer.class, jv);
        this.maxAttack = json.readValue("maxAttack", Integer.class, jv);
        this.level = json.readValue("level", Integer.class, jv);
        this.rarity = json.readValue("rarity", Integer.class, jv);
        this.maxHealth = json.readValue("maxHealth", Integer.class, jv);
        this.maxArmor = json.readValue("maxArmor", Integer.class, jv);
        this.runProgress = json.readValue("runProgress", Integer.class, jv);
        this.runRequirements = json.readValue("runRequirements", Integer.class, jv);
        this.ownerUID = json.readValue("ownerUID", String.class, jv);
        this.runComponents = json.readValue("runComponents", int[].class, jv);
        this.ability = json.readValue("ability", Ability.class, jv);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Card) {
            return ((Card) obj).getMatchID() == this.getMatchID();
        }
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.collection.getID());
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.matchID);
        hash = 79 * hash + Objects.hashCode(this.ownerUID);
        return hash;
    }

    @Override
    public int compareTo(Card o) {
        return this.hashCode() - o.hashCode();
    }

    @Override
    public String toString() {
        return name + ": [id: " + id + " matchID: " + matchID + " owner: " + ownerUID + " ]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        int[] runComponents = Arrays.copyOf(this.runComponents, this.runComponents.length);
        Card s = new Card(this.id, this.ownerUID, this.name, this.collection, this.textureName, this.level, this.maxHealth, this.maxArmor, this.maxAttack, this.magic, runComponents, this.runRequirements, this.ability, this.rarity);
        s.matchID = this.matchID;
        s.health = this.health;
        s.armor = this.armor;
        s.runProgress = this.runProgress;
        s.attack = this.attack;
        return s;
    }
}
