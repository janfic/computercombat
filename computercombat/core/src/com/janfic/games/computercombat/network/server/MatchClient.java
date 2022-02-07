package com.janfic.games.computercombat.network.server;

import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Profile;

/**
 *
 * @author Jan Fic
 */
public class MatchClient extends Client {

    private Profile profile;
    private Deck deck;

    public MatchClient(Profile profile, Deck deck, Client client) {
        super(client);
        this.profile = profile;
        this.deck = deck;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MatchClient) {
            MatchClient o = (MatchClient) obj;
            return o.getClientUID() == this.getClientUID();
        } else {
            return this.equals(obj);
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public Deck getDeck() {
        return deck;
    }
}
