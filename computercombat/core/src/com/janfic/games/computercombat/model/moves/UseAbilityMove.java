package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.GameRules;
import com.janfic.games.computercombat.model.match.MatchState;
import java.util.List;

public class UseAbilityMove extends Move implements Json.Serializable {

    private Card entity;
    private List<Component> selectedComponents;
    private List<Card> selectedSoftwares;

    public UseAbilityMove() {
        super("");
    }

    public UseAbilityMove(String playerUID, Card entity, List<Component> selectedComponents, List<Card> selectedSoftwares) {
        super(playerUID);
        this.entity = entity;
        this.selectedComponents = selectedComponents;
        this.selectedSoftwares = selectedSoftwares;
    }

    public Card getCard() {
        return entity;
    }

    public void setSelectedComponents(List<Component> selectedComponents) {
        this.selectedComponents = selectedComponents;
    }

    public void setSelectedSoftwares(List<Card> selectedSoftwares) {
        this.selectedSoftwares = selectedSoftwares;
    }

    public List<Component> getSelectedComponents() {
        return selectedComponents;
    }

    public List<Card> getSelectedSoftwares() {
        return selectedSoftwares;
    }

    /**
     * IMPORTANT: Move Results are used to RECORD state changes, not to apply
     * them
     *
     * @param state
     * @return
     */
    @Override
    public List<MoveResult> doMove(MatchState state) {
        List<MoveResult> results = entity.getAbility().doAbility(state, this);
        GameRules.isGameOver(state);
        return results;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("player", playerUID);
        json.writeValue("entity", entity, Card.class);
        json.writeValue("selectedComponents", selectedComponents, List.class);
        json.writeValue("selectedSoftwares", selectedSoftwares, List.class);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.playerUID = json.readValue("player", String.class, jv);
        this.entity = json.readValue("entity", Card.class, jv);
        this.selectedComponents = json.readValue("selectedComponents", List.class, jv);
        this.selectedSoftwares = json.readValue("selectedSoftwares", List.class, jv);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UseAbilityMove) {
            UseAbilityMove o = (UseAbilityMove) obj;
            return (o.entity.equals(this.entity) && o.playerUID.equals(this.playerUID));
        }
        return super.equals(obj);
    }
}
