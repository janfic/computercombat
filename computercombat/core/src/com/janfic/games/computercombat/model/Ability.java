package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.match.MatchState;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveAnimation;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.util.Filter;
import groovy.lang.GroovyShell;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

/**
 *
 * @author Jan Fic
 */
public abstract class Ability implements Serializable {

    protected String textureName, name, code;
    protected int id;
    protected String description;

    protected List<Filter> selectFilters;

    public Ability() {
        this.selectFilters = new ArrayList<>();
    }

    public Ability(List<Filter> selectFilters) {
        this.selectFilters = selectFilters;
    }

    /**
     * All changes or effects caused by an ability should be applied to the same
     * state object, but these changes should be recorded in the Move Results
     *
     * @param state
     * @param move
     * @return
     */
    public abstract List<MoveResult> doAbility(MatchState state, Move move);

    /**
     * Used when creating Ability from code in getAbilityFromCode(Ability).
     *
     * @param description
     * @param textureName
     * @param name
     * @param code
     * @param id
     */
    public void setInformation(String description, String textureName, String name, String code, int id) {
        this.id = id;
        this.name = name;
        this.textureName = textureName;
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTextureName() {
        return textureName;
    }

    public List<Filter> getSelectFilters() {
        return selectFilters;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("description", this.description);
        json.writeValue("textureName", this.textureName);
        json.writeValue("name", this.name);
        json.writeValue("code", this.code.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t"));
        json.writeValue("id", this.id);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.description = json.readValue("description", String.class, jsonData);
        this.textureName = json.readValue("textureName", String.class, jsonData);
        this.name = json.readValue("name", String.class, jsonData);
        this.code = json.readValue("code", String.class, jsonData).replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
        this.id = json.readValue("id", int.class, jsonData);
        this.selectFilters = getAbilityFromCode(this).getSelectFilters();
    }

    /**
     * Compiles code of an ability to set information for that ability that
     * wasn't loaded from DB.
     *
     * @param ability
     * @return
     */
    public static Ability getAbilityFromCode(Ability ability) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(new ImportCustomizer().addStarImports(
                "com.janfic.games.computercombat.model",
                "com.janfic.games.computercombat.model.abilities",
                "com.janfic.games.computercombat.model.components",
                "com.janfic.games.computercombat.model.moves",
                "com.janfic.games.computercombat.model.match",
                "com.janfic.games.computercombat.util"
        ));
        GroovyShell shell = new GroovyShell(config);
        Ability a = (Ability) shell.evaluate(ability.getCode());
        a.setInformation(
                ability.getDescription(), ability.getTextureName(), ability.getName(), ability.getCode(), ability.getID()
        );
        return a;
    }

    public static MoveAnimation consumeCardProgress(MatchState newState, Move move) {
        UseAbilityMove useAbilityMove = (UseAbilityMove) move;

        int index = newState.activeEntities.get(useAbilityMove.getPlayerUID()).indexOf(useAbilityMove.getCard());
        if (index != -1) {
            newState.activeEntities.get(useAbilityMove.getPlayerUID()).get(index).setProgress(0);

            List<Card> used = new ArrayList<>();
            used.add(useAbilityMove.getCard());
            return new ConsumeProgressAnimation(useAbilityMove.getPlayerUID(), used);
        } else {
            return null;
        }
    }
}
