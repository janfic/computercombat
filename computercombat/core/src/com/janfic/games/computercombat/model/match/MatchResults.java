package com.janfic.games.computercombat.model.match;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.Profile;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 *
 * @author Jan Fic
 */
public class MatchResults implements Json.Serializable {

    public int totalPacketsEarned;
    public Timestamp start, end;
    public Profile opponent;
    public boolean winner;
    public HashMap<String, String> rewards;

    public MatchResults() {
    }

    public MatchResults(int totalPacketsEarned, Timestamp start, Timestamp end, Profile opponent, boolean winner, HashMap<String, String> rewards) {
        this.totalPacketsEarned = totalPacketsEarned;
        this.start = start;
        this.end = end;
        this.opponent = opponent;
        this.winner = winner;
        this.rewards = rewards;
    }

    @Override
    public void write(Json json) {
        json.writeValue("totalPacketsEarned", this.totalPacketsEarned);
        json.writeValue("start", this.start.toString());
        json.writeValue("end", this.end.toString());
        json.writeValue("opponent", this.opponent);
        json.writeValue("winner", this.winner);
        json.writeValue("rewards", this.rewards);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.totalPacketsEarned = json.readValue("totalPacketsEarned", Integer.class, jsonData);
        this.start = Timestamp.valueOf(json.readValue("start", String.class, jsonData));
        this.end = Timestamp.valueOf(json.readValue("end", String.class, jsonData));
        this.opponent = json.readValue("opponent", Profile.class, jsonData);
        this.winner = json.readValue("winner", boolean.class, jsonData);
        this.rewards = json.readValue("rewards", HashMap.class, jsonData);
    }
}
