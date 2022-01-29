package com.janfic.games.computercombat.util;

import com.badlogic.gdx.utils.Json.Serializable;
import com.janfic.games.computercombat.model.Card;

/**
 *
 * @author Jan Fic
 */
public abstract class CardFilter implements Serializable, Filter<Card> {

    String description;

    public abstract boolean filter(Card card);

    @Override
    public String getDescription() {
        return description;
    }
}
