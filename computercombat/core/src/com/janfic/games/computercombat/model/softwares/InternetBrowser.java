package com.janfic.games.computercombat.model.softwares;

import com.janfic.games.computercombat.model.MatchState;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.components.NetworkComponent;

/**
 *
 * @author Jan Fic
 */
public class InternetBrowser extends Software {

    public InternetBrowser() {
        this(1);
    }
    
    public InternetBrowser(int level) {
        super(level, 3, 3, 3, 1, new Class[]{NetworkComponent.class, CPUComponent.class}, 10, null);
    }

    @Override
    public void beginMatch(MatchState state) {
    }

    @Override
    public void newTurn(MatchState state) {
    }
}
