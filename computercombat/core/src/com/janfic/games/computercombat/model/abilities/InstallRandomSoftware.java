package com.janfic.games.computercombat.model.abilities;

import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.MatchState;

/**
 *
 * @author Jan Fic
 */
public class InstallRandomSoftware extends Ability {

    @Override
    public String getDescription(int magic) {
        return "Install a random software to the users computer.";
    }

    @Override
    public boolean doAbility(int magic, MatchState state, Card target) {
        return false;
    }

}
