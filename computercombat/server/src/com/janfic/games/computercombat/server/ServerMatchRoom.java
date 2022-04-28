package com.janfic.games.computercombat.server;

import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Player;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.moves.MoveResult;
import com.janfic.games.computercombat.model.match.Match;
import com.janfic.games.computercombat.model.match.MatchData;
import com.janfic.games.computercombat.model.match.MatchResults;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.moves.UseAbilityMove;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.util.NullifyingJson;
import java.util.List;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 *
 * @author Jan Fic
 */
public class ServerMatchRoom {

    private Player player1, player2;
    private Match match;
    private MatchData matchData;
    private boolean isGameOver;
    private Thread thread;

    public ServerMatchRoom(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Begin the match
                match = new Match(player1, player2);
                player1.beginMatch(match.getPlayerMatchState(player1.getUID()), player2);
                player2.beginMatch(match.getPlayerMatchState(player2.getUID()), player1);

                // set up match data collection
                Timestamp starttime = new Timestamp(System.currentTimeMillis());
                matchData = new MatchData(player1, player2);
                matchData.getMatchStates().add(MatchState.record(match.getCurrentState()));

                // Start main match loop
                isGameOver = false;
                while (isGameOver == false) {
                    // get move of current player
                    String whosMoveUID = match.whosMove();
                    Player currentPlayer = whosMoveUID.equals(player1.getUID()) ? player1 : player2;
                    Player otherPlayer = !whosMoveUID.equals(player1.getUID()) ? player1 : player2;

                    Move move = currentPlayer.getMove();

                    if (move == null) {
                        isGameOver = true;
                        break;
                    }

                    // apply the move
                    if (move instanceof UseAbilityMove) {
                        UseAbilityMove m = (UseAbilityMove) move;
                        for (Card c : match.getCurrentState().activeEntities.get(m.getPlayerUID())) {
                            if (c.getMatchID() == m.getCard().getMatchID()) {
                                m.getCard().setAbility(Ability.getAbilityFromCode(c.getAbility()));
                            }
                        }
                        if (m.getCard().getID() == 0) {
                            m.getCard().setAbility(Ability.getAbilityFromCode(match.getCurrentState().computers.get(m.getPlayerUID()).getAbility()));
                        }
                    }
                    boolean isValid = match.isValidMove(move);
                    if (isValid) {
                        System.out.println(move.getPlayerUID() + ": " + move.getClass());
                        List<MoveResult> results = match.makeMove(move);
                        currentPlayer.updateState(results);
                        otherPlayer.updateState(results);
                        matchData.add(move, results, match.getCurrentState());
                    }

                    isGameOver = match.getCurrentState().isGameOver;
                }

                //Build MatchData
                Timestamp endtime = new Timestamp(System.currentTimeMillis());
                MatchState lastState = matchData.getMatchStates().get(matchData.getMatchStates().size() - 1);
                if (lastState.winner != null) {
                    matchData.setWinner(lastState.winner.equals(player2.getUID()));
                } else {
                    matchData.setWinner(false);
                }

                matchData.setStartTime(starttime);
                matchData.setEndTime(endtime);

                // Calculate Rewards
                HashMap<String, String> rewards1 = new HashMap<>();
                HashMap<String, String> rewards2 = new HashMap<>();
                calculateRewards(rewards1, rewards2);

                MatchResults results1 = new MatchResults(matchData.getRewards().get(player1.getUID()), starttime, endtime, player2, !matchData.getWinner(), rewards1);
                MatchResults results2 = new MatchResults(matchData.getRewards().get(player2.getUID()), starttime, endtime, player1, matchData.getWinner(), rewards2);

                player1.gameOver(results1);
                player2.gameOver(results2);

                // Insert Match Data to DB
                // Update Profiles ( packets )
                System.out.println("END OF MATCH");

                Profile profile1 = SQLAPI.getSingleton().loadProfile(player1.getUID());
                Profile profile2 = SQLAPI.getSingleton().loadProfile(player2.getUID());
                profile2.setPackets(profile2.getPackets() + matchData.getRewards().get(profile2.getUID()));
                profile1.setPackets(profile1.getPackets() + matchData.getRewards().get(profile1.getUID()));

                // Insert Match Data to DB
                int updates = SQLAPI.getSingleton().recordMatchData(matchData);
                SQLAPI.getSingleton().saveProfile(profile1);
                SQLAPI.getSingleton().saveProfile(profile2);
            }
        });
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void start() {
        thread.start();
    }

    public void calculateRewards(HashMap<String, String> rewards1, HashMap<String, String> rewards2) {
        rewards1.put("Collected", "" + matchData.getRewards().get(player1.getUID()));
        rewards2.put("Collected", "" + matchData.getRewards().get(player2.getUID()));
        if (matchData.getWinner()) {
            matchData.getRewards().put(player2.getUID(), matchData.getRewards().get(player2.getUID()) + 25);
            rewards2.put("Victory Bonus", "" + 25);
        } else {
            matchData.getRewards().put(player1.getUID(), matchData.getRewards().get(player1.getUID()) + 25);
            rewards1.put("Victory Bonus", "" + 25);
        }
    }

    public MatchData getMatchData() {
        return matchData;
    }
}
