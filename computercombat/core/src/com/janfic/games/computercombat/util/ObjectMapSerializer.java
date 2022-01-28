package com.janfic.games.computercombat.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class ObjectMapSerializer<K, V extends List> implements Json.Serializer<ObjectMap<K, V>> {

    @Override
    public void write(Json json, ObjectMap<K, V> object, Class knownType) {
        json.writeObjectStart();

        json.writeArrayStart("entries");

        for (Entry<?, ?> entry : object.entries()) {
            json.writeObjectStart();
            json.writeValue("key", entry.key, entry.key.getClass());
            json.writeValue("value", entry.value, entry.value.getClass());
            json.writeObjectEnd();
        }

        json.writeArrayEnd();
        json.writeObjectEnd();

    }

    @Override
    public ObjectMap<K, V> read(Json json, JsonValue jsonData, Class type) {

        ObjectMap<K, V> result = new ObjectMap<>();

        JsonValue entries = jsonData.child;
        for (JsonValue child = entries.child; child != null; child = child.next) {
            result.put((K) json.readValue(null, child.get("key")), (V) json.readValue(List.class, child.get("value")));
        }

        return result;
    }
}
