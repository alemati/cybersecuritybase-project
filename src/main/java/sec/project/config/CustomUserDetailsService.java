package sec.project.config;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.User;
import sec.project.repository.BankRepository;
import sec.project.repository.UserRepository;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private Map<String, String> accountDetails;
    private String dbA;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankRepository bankRepository;

    @PostConstruct
    public void init() {
        this.dbA = "jdbc:h2:file:./db/database";

        try (Connection connection = DriverManager.getConnection(this.dbA, "sa", "")) {
            if (!databaseExists(connection, "USER")) {
                RunScript.execute(connection, new FileReader("db/database-schema.sql"));
                //RunScript.execute(connection, new FileReader("db/database-import.sql"));
            }

        } catch (Exception e) {
            System.out.println("Database error: " + e);
        }

        try (Connection connection = DriverManager.getConnection(this.dbA, "sa", "")) {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM User");
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                userRepository.save(user);
            }

            // todo Banks save
        } catch (Exception e) {
            System.out.println("Database error: " + e);
        }
    }

    private boolean databaseExists(Connection connection, String name) throws SQLException {
        DatabaseMetaData dbmd = connection.getMetaData();
        String[] tab = {"TABLE"};
        ResultSet tables = dbmd.getTables(null, null, null, tab);
        while (tables.next()) {
            if (tables.getString("TABLE_NAME").equals(name)) {
                return true;
            }
        }
        return false;
    }
    public void saveUserToDatabase(Long id, String un, String pw) {
        try (Connection connection = DriverManager.getConnection(this.dbA, "sa", "")) {
            String query2 = "INSERT INTO User (id, username, password) VALUES ("+ id + ", '" + un+ "', '" + pw + "')";
            connection.createStatement().executeUpdate(query2);
        } catch (Exception e) {
            System.out.println("Error while inserting user to db: " + e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!this.accountDetails.containsKey(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                username,
                this.accountDetails.get(username),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
