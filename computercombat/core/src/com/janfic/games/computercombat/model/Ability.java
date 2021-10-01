package com.janfic.games.computercombat.model;

import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.MoveResult;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public abstract class Ability {

    protected String textureName, name, code;
    protected int id;
    protected String description;
    protected int selectComponents, selectSoftwares;

    public Ability(int selectComponents, int selectSoftwares) {
        this.selectComponents = selectComponents;
        this.selectSoftwares = selectSoftwares;
    }

    public abstract List<MoveResult> doAbility(MatchState state, Move move);

    public void setInformation(String description, String textureName, String name, String code, int id) {
        this.id = id;
        this.name = name;
        this.textureName = textureName;
        this.code = code;
        this.description = description;
    }

    public int getSelectComponents() {
        return selectComponents;
    }

    public int getSelectSoftwares() {
        return selectSoftwares;
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
}
