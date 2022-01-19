package com.janfic.games.computercombat.model.match;

import com.janfic.games.computercombat.model.Profile;
import java.sql.Timestamp;

/**
 *
 * @author Jan Fic
 */
public class MatchResults {

    public int packetsEarned;
    public Timestamp start, end;
    public Profile opponent;
    public boolean winner;
}
