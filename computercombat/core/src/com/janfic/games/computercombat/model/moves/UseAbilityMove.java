package com.janfic.games.computercombat.model.moves;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.MatchState;
import java.util.ArrayList;
import java.util.List;

public class UseAbilityMove extends Move {

    private Card entity, target;
    private List<Component> selectedComponents;
    private List<Card> selectedSoftwares;

    public UseAbilityMove() {
        super("");
    }

    public UseAbilityMove(String playerUID, Card entity, Card target) {
        super(playerUID);
        this.entity = entity;
        this.target = target;
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

    @Override
    public List<MoveResult> doMove(MatchState state) {
        List<MoveResult> results = new ArrayList<>();
        //stubbed
        return results;
    }

    @Override
    public void write(Json json) {
        json.writeValue("player", playerUID);
        json.writeValue("entity", entity, Card.class);
        json.writeValue("target", target, Card.class);
        json.writeValue("selectedComponents", selectedComponents, List.class);
        json.writeValue("selectedSoftwares", selectedSoftwares, List.class);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.playerUID = json.readValue("player", String.class, jv);
        this.entity = json.readValue("entity", Card.class, jv);
        this.target = json.readValue("target", Card.class, jv);
        this.selectedComponents = json.readValue("selectedComponents", List.class, jv);
        this.selectedSoftwares = json.readValue("selectedSoftwares", List.class, jv);
    }
}
