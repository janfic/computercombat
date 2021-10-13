package com.janfic.games.computercombat.network.client;

import com.badlogic.gdx.Gdx;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
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

    String url = "jdbc:mysql://computer-combat-db.cloqezbutiub.us-east-1.rds.amazonaws.com:3306";

    private static SQLAPI singleton;
    private Properties properties;
    private Connection connection;
    private GroovyShell shell;

    private SQLAPI() {
        this.properties = new Properties();
        try {
            this.properties.load(Gdx.files.internal("sql.properties").read());
            connection = DriverManager.getConnection(url, properties);
            Statement statement = connection.createStatement();
            statement.execute("USE computer_combat;");
            CompilerConfiguration config = new CompilerConfiguration();
            config.addCompilationCustomizers(new ImportCustomizer().addStarImports(
                    "com.janfic.games.computercombat.model",
                    "com.janfic.games.computercombat.model.abilities",
                    "com.janfic.games.computercombat.model.components",
                    "com.janfic.games.computercombat.model.moves"
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

        Map<Card, Integer> cards = new HashMap<>();

        try {
            //Get all player owned cards
            String sql = "SELECT card.* \n"
                    + "FROM card \n"
                    + "JOIN profile_owns_card ON card.id = profile_owns_card.card_id \n"
                    + "JOIN profile ON profile.uid = profile_owns_card.profile_id \n"
                    + "WHERE profile.uid = '" + uid + "' \n"
                    + "ORDER BY card.name;";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);
            while (set.next()) {

                //Get Components of Card
                sql = "SELECT components.* \n"
                        + "FROM card \n"
                        + "JOIN run_requirements ON card.id = run_requirements.card_id \n"
                        + "JOIN components ON components.id = run_requirements.component_id \n"
                        + "WHERE card.id = " + set.getInt("card.id");

                Statement getComponentStatement = connection.createStatement();
                ResultSet gcResults = getComponentStatement.executeQuery(sql);

                List<Class<? extends Component>> components = new ArrayList<>();
                while (gcResults.next()) {
                    components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + gcResults.getString("components.name")));
                }

                Ability a = getAbilityByID(set.getInt("card.ability_id"));

                Software s = new Software(
                        set.getInt("card.id"),
                        set.getString("card.name"),
                        "computer_pack",
                        set.getString("textureName"),
                        set.getInt("level"),
                        set.getInt("maxHealth"),
                        set.getInt("maxDefense"),
                        set.getInt("maxAttack"),
                        1,
                        components.toArray(new Class[0]),
                        set.getInt("card.runRequirements"),
                        a
                );
                cards.put(s, cards.getOrDefault(s, 0) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Software getCardById(int id) {
        try {
            String sql = "SELECT card.* \n"
                    + "FROM card \n"
                    + "WHERE card.id = " + id + ";";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            set.next();
            //Get Components of Card
            sql = "SELECT components.* \n"
                    + "FROM card \n"
                    + "JOIN run_requirements ON card.id = run_requirements.card_id \n"
                    + "JOIN components ON components.id = run_requirements.component_id \n"
                    + "WHERE card.id = " + set.getInt("card.id");

            Statement getComponentStatement = connection.createStatement();
            ResultSet gcResults = getComponentStatement.executeQuery(sql);

            List<Class<? extends Component>> components = new ArrayList<>();
            while (gcResults.next()) {
                components.add((Class<? extends Component>) Class.forName("com.janfic.games.computercombat.model.components." + gcResults.getString("components.name")));
            }

            Ability a = getAbilityByID(set.getInt("card.ability_id"));

            return new Software(
                    set.getInt("card.id"),
                    set.getString("card.name"),
                    "computer_pack",
                    set.getString("textureName"),
                    set.getInt("level"),
                    set.getInt("maxHealth"),
                    set.getInt("maxDefense"),
                    set.getInt("maxAttack"),
                    1,
                    components.toArray(new Class[0]),
                    set.getInt("card.runRequirements"),
                    a
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Ability getAbilityByID(int id) {
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
        List<Deck> decks = new ArrayList<>();

        try {
            //Query for decks owned by player
            String sql = "SELECT * FROM deck\n"
                    + "WHERE deck.profile_id = '" + uid + "';";

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql);

            while (set.next()) {

                int id = set.getInt("id");
                String name = set.getString("name");

                sql = "SELECT * \n"
                        + "FROM deck_has_card \n"
                        + "WHERE deck_has_card.deck_id = " + id + ";";

                Statement stat = connection.createStatement();
                ResultSet rs = stat.executeQuery(sql);

                Deck deck = new Deck(name, id);

                while (rs.next()) {
                    Software c = getCardById(rs.getInt("card_id"));
                    deck.addCard(c, 1);
                }
                decks.add(deck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decks;
    }

    public void savePlayerDeck(Deck deck, String uid) {
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

            for (Integer card : deck.getCards()) {

                for (int i = 0; i < deck.getCardCount(card); i++) {
                    sql = "INSERT INTO deck_has_card (deck_id, card_id) \n"
                            + "VALUES (" + deck.getID() + "," + card + ");";

                    rows = statement.executeUpdate(sql);
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerDeck(Deck deck, String uid) {
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
            }

            System.out.println(profile.getName() + " " + profile.getUID());

            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProfile(Profile p) {
        try {

            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + p.getUID() + "';";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                System.out.println("SAVE PROFILE");

            } else {
                sql = "INSERT INTO profile (uid, username, email) \n"
                        + "VALUES ('" + p.getUID() + "', '" + p.getName() + "', '" + p.getEmail() + "');";
            }
            //profile table
            statement = connection.createStatement();
            int rowsUpdated = statement.executeUpdate(sql);

            //decks
            for (Deck deck : p.getDecks()) {

            }

            //cards
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addCardToProfile(int cardID, Profile profile) {
        try {
            String sql = "SELECT uid\n"
                    + "FROM profile\n"
                    + "WHERE profile.uid = '" + profile.getUID() + "';";

            System.out.println(sql);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            boolean exists = rs.next();

            if (exists) {
                sql = "INSERT INTO profile_owns_card (profile_id, card_id)\n"
                        + "VALUES ('" + profile.getUID() + "'," + cardID + ");";
                System.out.println(sql);
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
}
