package com.janfic.games.computercombat.network.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.match.MatchData;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.moves.MoveResult;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

/**
 *
 * @author Jan Fic
 */
public class SQLAPI {

//    String url = "jdbc:mysql://computer-combat-db.cloqezbutiub.us-east-1.rds.amazonaws.com:3306";
//    String url = "jdbc:mysql://137.184.137.169:30306";
    String url = "jdbc:mysql://localhost:30306";

    private static SQLAPI singleton;
    private Properties properties;
    private Connection connection;
    private GroovyShell shell;

    private SQLAPI() {
        this.properties = new Properties();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.properties.load(Gdx.files.internal("sql.properties").read());
            connection = DriverManager.getConnection(url, properties);
            Statement statement = connection.createStatement();
            statement.execute("USE computer_combat;");
            CompilerConfiguration config = new CompilerConfiguration();
            config.addCompilationCustomizers(new ImportCustomizer().addStarImports(
                    "java.util",
                    "com.janfic.games.computercombat.model",
                    "com.janfic.games.computercombat.network.client",
                    "com.janfic.games.computercombat.model.abilities",
                    "com.janfic.games.computercombat.model.components",
                    "com.janfic.games.computercombat.model.moves",
                    "com.janfic.games.computercombat.model.match",
                    "com.janfic.games.computercombat.util"
            ));
            shell = new GroovyShell(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static SQLAPI getSingleton() {
        if (singleton == null) {
            singleton = new SQLAPI();
        }
        return singleton;
    }

    public Map<Card, Integer> getPlayerOwnedCards(String uid) {
        System.out.println("[SERVER][MYSQL]: Querying for Player collection");
        Map<Card, Integer> cards = new HashMap<>();

        try {
            //Get all player owned cards
            String sql = "SELECT * FROM card \n"
                    + "JOIN profile_owns_card ON card.id = profile_owns_card.card_id \n"
                    + "JOIN profile ON profile.uid = profile_owns_card.profile_id \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE profile.uid = '" + uid + "' ORDER BY card.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean next = set.next();
            while (next) {
                int currentID = set.getInt("card.id");

                Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
                a.setInformation(
                        set.getString("ability.description"),
                        set.getString("ability.textureName"),
                        set.getString("ability.name"),
                        set.getString("ability.code"),
                        set.getInt("ability.id")
                );

                Collection c = new Collection(
                        set.getInt("collection.id"),
                        set.getString("collection.name"),
                        set.getString("collection.description"),
                        set.getString("collection.textureName"),
                        set.getString("collection.path"),
                        set.getInt("collection.price"));

                List<Integer> components = new ArrayList<>();

                int id = set.getInt("card.id");
                String name = set.getString("card.name");
                String textureName = set.getString("card.textureName");
                int level = set.getInt("card.level");
                int maxHealth = set.getInt("card.maxHealth");
                int maxDefense = set.getInt("card.maxDefense");
                int maxAttack = set.getInt("card.maxAttack");
                int runRequirements = set.getInt("card.runRequirements");
                int rarity = set.getInt("card.rarity");
                int amount = set.getInt("profile_owns_card.amount");
                String description = set.getString("card.description");

                do {
                    components.add(set.getInt("components.id"));
                    next = set.next();
                } while (next && currentID == set.getInt("card.id"));

                int[] componentTypes = new int[components.size()];
                for (int i = 0; i < components.size(); i++) {
                    componentTypes[i] = components.get(i);
                }

                Card s = new Card(
                        id,
                        uid,
                        name,
                        c,
                        textureName,
                        level,
                        maxHealth,
                        maxDefense,
                        maxAttack,
                        1, // magic
                        componentTypes,
                        runRequirements,
                        a,
                        rarity,
                        description
                );
                cards.put(s, amount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Card getCardById(int id, String optionalUID) {
        System.out.println("[SERVER][MYSQL]: Querying for Card Data");
        try {
            String sql = "SELECT * FROM card \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE card.id = '" + id + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            set.next();

            Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
            a.setInformation(
                    set.getString("ability.description"),
                    set.getString("ability.textureName"),
                    set.getString("ability.name"),
                    set.getString("ability.code"),
                    set.getInt("ability.id")
            );

            Collection c = new Collection(
                    set.getInt("collection.id"),
                    set.getString("collection.name"),
                    set.getString("collection.description"),
                    set.getString("collection.textureName"),
                    set.getString("collection.path"),
                    set.getInt("collection.price"));

            List<Integer> components = new ArrayList<>();

            String name = set.getString("card.name");
            String textureName = set.getString("card.textureName");
            int level = set.getInt("card.level");
            int maxHealth = set.getInt("card.maxHealth");
            int maxDefense = set.getInt("card.maxDefense");
            int maxAttack = set.getInt("card.maxAttack");
            int runRequirements = set.getInt("card.runRequirements");
            int rarity = set.getInt("card.rarity");
            String description = set.getString("card.description");

            do {
                components.add(set.getInt("components.id"));
            } while (set.next());

            int[] componentTypes = new int[components.size()];
            for (int i = 0; i < components.size(); i++) {
                componentTypes[i] = components.get(i);
            }

            Card s = new Card(
                    id,
                    optionalUID,
                    name,
                    c,
                    textureName,
                    level,
                    maxHealth,
                    maxDefense,
                    maxAttack,
                    1,
                    componentTypes,
                    runRequirements,
                    a,
                    rarity,
                    description
            );
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Card> getCardsInfo(List<Integer> cardIDs, String optionalUID) {
        List<Card> cards = new ArrayList<>();
        try {
            String sql = "SELECT *, group_concat(run_requirements.component_id) as components\n"
                    + "FROM card \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id GROUP BY card.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();

            while (areRowsLeft) {
                Card c = readCardFromSet(set, optionalUID);
                if (cardIDs.contains(c.getID())) {
                    cards.add(c);
                }
                areRowsLeft = set.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return cards;
        }
        return cards;
    }

    public Deck getDeck(int deckID, String uid) {
        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.profile_id = '" + uid + "' AND deck.id = '" + deckID + "';";
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            if (set.next() == false) {
                return null;
            }

            Deck deck = new Deck(set.getString("deck.name"), deckID);

            sql = "SELECT *, group_concat(run_requirements.component_id) as components\n"
                    + "FROM deck_has_card \n"
                    + "JOIN card ON deck_has_card.card_id = card.id\n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE deck_has_card.deck_id = '" + deckID + "' GROUP BY card.id;";

            set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();
            while (areRowsLeft) {
                Card c = readCardFromSet(set, uid);
                int amount = set.getInt("deck_has_card.amount");
                deck.addCard(c, amount);
                areRowsLeft = set.next();
            }

            return deck;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Card> getCardsInDeck(int deckID, String uid) {
        List<Card> cards = new ArrayList<>();

        try {
            String sql = "SELECT *, group_concat(run_requirements.component_id) as components\n"
                    + "FROM deck_has_card \n"
                    + "JOIN card ON deck_has_card.card_id = card.id\n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id\n"
                    + "WHERE deck_has_card.deck_id = '" + deckID + "' GROUP BY card.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();
            while (areRowsLeft) {
                Card c = readCardFromSet(set, uid);
                cards.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    public Ability getAbilityByID(int id) {
        System.out.println("[SERVER][MYSQL]: Querying for Ability Data");
        try {
            String sql = "SELECT * \n"
                    + "FROM ability \n"
                    + "JOIN card ON card.ability_id = ability.id \n"
                    + "WHERE ability.id = " + id + ";";

            Statement getAbilityStatement = connection.createStatement();
            ResultSet gAResults = getAbilityStatement.executeQuery(sql);

            gAResults.next();

            Ability a = (Ability) shell.evaluate(gAResults.getString("code"));
            a.setInformation(
                    gAResults.getString("description"),
                    gAResults.getString("textureName"),
                    gAResults.getString("name"),
                    gAResults.getString("code"),
                    gAResults.getInt("id")
            );
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Deck> getPlayerDecks(String uid) {
        System.out.println("[SERVER][MYSQL]: Querying for Player Decks Data");
        List<Deck> decks = new ArrayList<>();

        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.profile_id = '" + uid + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            while (set.next()) {
                int deckID = set.getInt("deck.id");
                decks.add(getDeck(deckID, uid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decks;
    }

    public void savePlayerDeck(Deck deck, String uid) {
        System.out.println("[SERVER][MYSQL]: Updating Player Deck Data");
        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.id = " + deck.getID() + ";";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            int rows = 0;

            boolean exists = set.next();
            if (exists) {

                sql = "UPDATE deck "
                        + "SET name = '" + deck.getName() + "', profile_id = '" + uid + "' \n"
                        + "WHERE id = " + deck.getID() + ";";

                rows = statement.executeUpdate(sql);
            } else {
                sql = "INSERT INTO deck (id, name, profile_id) \n"
                        + "VALUES (" + deck.getID() + " , '" + deck.getName() + "', '" + uid + "');";

                rows = statement.executeUpdate(sql);

            }

            sql = "DELETE FROM deck_has_card \n"
                    + "WHERE deck_has_card.deck_id = " + deck.getID() + ";";

            rows = statement.executeUpdate(sql);

            System.out.println(deck.getStack());

            for (String card : deck.getCards().keySet()) {
                sql = "INSERT INTO deck_has_card (deck_id, card_id, amount) \n"
                        + "VALUES (" + deck.getID() + "," + card + "," + deck.getCardCount(Integer.parseInt(card)) + ");";

                rows = statement.executeUpdate(sql);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerDeck(Deck deck, String uid) {
        System.out.println("[SERVER][MYSQL]: Deleting Player Deck");
        try {
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.id = " + deck.getID() + ";";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            int rows;

            boolean exists = set.next();
            if (exists) {

                sql = "DELETE FROM deck_has_card \n"
                        + "WHERE deck_has_card.deck_id = " + deck.getID() + ";";
                rows = statement.executeUpdate(sql);

                sql = "DELETE FROM move \n"
                        + "WHERE match_id = (SELECT id FROM `match` WHERE deck1_id = '" + deck.getID() + "' OR deck2_id = '" + deck.getID() + "' );";
                rows = statement.executeUpdate(sql);

                sql = "DELETE FROM computer_combat.`match` \n"
                        + "WHERE `match`.deck1_id = '" + deck.getID() + "' OR `match`.deck2_id = '" + deck.getID() + "';";
                rows = statement.executeUpdate(sql);

                sql = "DELETE FROM deck "
                        + "WHERE id = " + deck.getID() + ";";

                rows = statement.executeUpdate(sql);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Profile loadProfile(String uid) {
        System.out.println("[SERVER][MYSQL]: Loading Player Profile");
        try {
            String sql = "SELECT *\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + uid + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            Profile profile = new Profile(uid);

            while (rs.next()) {
                profile.setName(rs.getString("username"));
                profile.setEmail(rs.getString("email"));
                profile.setPackets(rs.getInt("packets"));
            }
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProfile(Profile p) {
        System.out.println("[SERVER][MYSQL]: Saving Player Profile");
        try {

            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + p.getUID() + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                sql = "UPDATE profile SET packets = " + p.getPackets() + " WHERE profile.uid = '" + p.getUID() + "';";
            } else {
                sql = "INSERT INTO profile (uid, username, email, packets) \n"
                        + "VALUES ('" + p.getUID() + "', '" + p.getName() + "', '" + p.getEmail() + "', " + p.getPackets() + ");";
            }
            //profile table
            statement = connection.createStatement();
            int rowsUpdated = statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCardsToProfile(Map<Integer, Integer> cards, Profile profile) {
        try {

            for (Integer id : cards.keySet()) {

                String sql = "SELECT * FROM profile_owns_card \n"
                        + "WHERE profile_id = '" + profile.getUID() + "' AND card_id = '" + id + "';";

                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);

                if (rs.next()) {
                    sql = "UPDATE profile_owns_card SET amount = amount + " + cards.get(id) + "\n"
                            + "WHERE profile_id = '" + profile.getUID() + "' AND card_id = " + id + ";";

                    System.out.println("UPDATE");

                    int rowsUpdated = statement.executeUpdate(sql);
                } else {
                    sql = "INSERT INTO profile_owns_card (profile_id, card_id, amount)\n"
                            + "VALUES ('" + profile.getUID() + "'," + id + "," + cards.get(id) + ");";

                    int rowsUpdated = statement.executeUpdate(sql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addCardToProfile(int cardID, Profile profile) {
        try {
            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + profile.getUID() + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                sql = "INSERT INTO profile_owns_card (profile_id, card_id)\n"
                        + "VALUES ('" + profile.getUID() + "'," + cardID + ");";
                int rowsUpdated = statement.executeUpdate(sql);
                return rowsUpdated >= 1;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int recordMatchData(MatchData data) {
        System.out.println("[SERVER][MYSQL]: Recording Match Data");
        Json json = new Json(JsonWriter.OutputType.json);
        int r = 0;
        try {
            r = 0;
            // Insert New Match Record
            String sql = "INSERT INTO computer_combat.`match` (player1_uid, player2_uid, deck1_id, deck2_id, winner, starttime, endtime, packets_player1, packets_player2)\n"
                    + "VALUES ('"
                    + data.getPlayer1().getUID() + "' , '"
                    + data.getPlayer2().getUID() + "' , "
                    + data.getPlayer1().getActiveDeck().getID() + " , "
                    + data.getPlayer2().getActiveDeck().getID() + " , "
                    + (data.getWinner() ? 1 : 0) + " , "
                    + "str_to_date('" + data.getStartTime().toString() + "', '%Y-%m-%d %H:%i:%s.%f'), "
                    + "str_to_date('" + data.getEndTime().toString() + "', '%Y-%m-%d %H:%i:%s.%f'),"
                    + data.getRewards().get(data.getPlayer1().getUID()) + ","
                    + data.getRewards().get(data.getPlayer2().getUID()) + ""
                    + ");";

            Statement statement = connection.createStatement();
            int updates = statement.executeUpdate(sql);

            // Get Generated Match ID
            sql = "SELECT LAST_INSERT_ID();";
            ResultSet results = statement.executeQuery(sql);
            results.next();
            int match_id = results.getInt(1);

            for (int i = 0; i < data.getMoves().size(); i++) {
                // Insert Move Results
                List<MoveResult> moveResults = data.getMoveResults().get(i);

                // Insert Move
                sql = "INSERT INTO move (`data`, `match_id`, `move_number`) "
                        + "VALUES (JSON_QUOTE('" + json.toJson(moveResults) + "')," + match_id + "," + (i + 1) + ");";
                updates = statement.executeUpdate(sql);
                r += updates;
            }

            return updates;

        } catch (Exception e) {
            e.printStackTrace();
            return r;
        }
    }

    public void dispose() {
        closeConnection();
        singleton = null;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<>();

        try {
            String sql = "SELECT * FROM collection;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            while (set.next()) {
                Collection c = new Collection(
                        set.getInt("id"),
                        set.getString("name"),
                        set.getString("description"),
                        set.getString("textureName"),
                        set.getString("path"),
                        set.getInt("price")
                );

                collections.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return collections;
    }

    public List<Card> getCardsInCollection(List<Integer> collectionIDs, String optionalUID) {
        List<Card> cards = new ArrayList<>();

        try {
            String sql = "SELECT *, group_concat(run_requirements.component_id) as components FROM card \n"
                    + "JOIN ability ON card.ability_id = ability.id\n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id\n"
                    + "JOIN components ON components.id = run_requirements.component_id\n"
                    + "JOIN collection ON card.collection_id = collection.id GROUP BY card.id;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            boolean areRowsLeft = set.next();

            while (areRowsLeft) {
                if (collectionIDs.contains(set.getInt("card.collection_id")) && set.getInt("card.id") != 0) {
                    Card c = readCardFromSet(set, optionalUID);
                    cards.add(c);
                }
                areRowsLeft = set.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    private Card readCardFromSet(ResultSet set, String uid) {
        try {

            Ability a = (Ability) shell.evaluate(set.getString("ability.code"));
            a.setInformation(
                    set.getString("ability.description"),
                    set.getString("ability.textureName"),
                    set.getString("ability.name"),
                    set.getString("ability.code"),
                    set.getInt("ability.id")
            );

            Collection collection = new Collection(
                    set.getInt("collection.id"),
                    set.getString("collection.name"),
                    set.getString("collection.description"),
                    set.getString("collection.textureName"),
                    set.getString("collection.path"),
                    set.getInt("collection.price"));

            String name = set.getString("card.name");
            String textureName = set.getString("card.textureName");
            int level = set.getInt("card.level");
            int id = set.getInt("card.id");
            int maxHealth = set.getInt("card.maxHealth");
            int maxDefense = set.getInt("card.maxDefense");
            int maxAttack = set.getInt("card.maxAttack");
            int runRequirements = set.getInt("card.runRequirements");
            int rarity = set.getInt("card.rarity");
            String description = set.getString("card.description");
            String components = set.getString("components");
            String[] c = components.split(",");

            int[] componentTypes = new int[c.length];
            for (int i = 0; i < c.length; i++) {
                componentTypes[i] = Integer.parseInt(c[i]);
            }

            Card s = new Card(
                    id,
                    uid,
                    name,
                    collection,
                    textureName,
                    level,
                    maxHealth,
                    maxDefense,
                    maxAttack,
                    1,
                    componentTypes,
                    runRequirements,
                    a,
                    rarity,
                    description
            );

            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void pingDatabase() {
        try {
            String sql = "SELECT 1;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            set.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
