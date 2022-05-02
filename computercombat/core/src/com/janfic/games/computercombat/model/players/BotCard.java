package com.janfic.games.computercombat.model.players;

import com.janfic.games.computercombat.model.players.heuristicanalyzers.HeuristicAnalyzer;

public class BotCard {

    private int id;
    private String name;
    private String textureName;
    private String description;
    private String code;

    private HeuristicAnalyzer heuristicAnalyzer;

    public BotCard() {

    }

    public BotCard(int id, String name, String textureName, String description, String code) {
        this.id = id;
        this.name = name;
        this.textureName = textureName;
        this.description = description;
        this.code = code;

        this.heuristicAnalyzer = HeuristicAnalyzer.getHeuristicAnalyzerFromCode(code);
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTextureName() {
        return this.textureName;
    }

    public String getDescription() {
        return this.description;
    }

    public HeuristicAnalyzer getHeuristicAnalyzer() {
        return this.heuristicAnalyzer;
    }

    public String toString() {
        return "BotCard: [ id: " + id
                + " , name: " + name
                + " , description: " + description
                + " , heuristicAnalyzer: " + heuristicAnalyzer
                + " , code: " + code + "]";
    }
}
