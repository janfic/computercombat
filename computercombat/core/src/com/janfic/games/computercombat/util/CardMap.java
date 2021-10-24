package com.janfic.games.computercombat.util;

import com.janfic.games.computercombat.model.Card;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Jan Fic
 */
public class CardMap<T> {

    private Map<Card, T> cardMap;

    public CardMap() {
        this.cardMap = new TreeMap<>();
    }

    public Map<Card, T> getCardMap() {
        return cardMap;
    }
}
