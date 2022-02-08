package com.janfic.games.computercombat.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 *
 * @author Jan Fic
 */
public class NullifyingJson extends Json {

    @Override
    public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {
        if (jsonData == null) {
            return null;
        }
        if (jsonData.isNull()) {
            return null;
        }
        return super.readValue(type, elementType, jsonData); //To change body of generated methods, choose Tools | Templates.
    }

}
