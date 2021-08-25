package com.janfic.games.computercombat.model;

/**
 *
 * @author Jan Fic
 */
public abstract class Ability {

    public abstract String getDescription(int magic);

    public abstract boolean doAbility(int magic, MatchState state, Card target);
}
