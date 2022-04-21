package com.janfic.games.computercombat.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Jan Fic
 */
public class ObjectMapSerializer implements Json.Serializer<Map> {

    @Override
    public void write(Json json, Map object, Class knownType) {
        json.writeObjectStart();

        for (Object key : object.keySet()) {
            json.writeObjectStart();
            json.writeValue(key.toString(), object.get(key));
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        json.writeObjectEnd();

    }

    @Override
    public Map read(Json json, JsonValue jsonData, Class type) {

        Map result = new HashMap<>();

        System.out.println("HERERERERER");

        JsonValue entries = jsonData.child;
        for (JsonValue child = entries.child; child != null; child = child.next) {
            Object key = json.readValue(null, child.get("key"));
            Object value = json.readValue(null, child.get("value"));
            if (value instanceof Array) {
                List arrayList = new ArrayList<>();
                for (Object object : (Array) value) {
                    arrayList.add(object);
                }
                value = arrayList;
            }
            result.put(
                    key, value
            );

        }
        return result;
    }
}
