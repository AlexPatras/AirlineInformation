package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class DBController {

    static Map<String, DataSource> cache = new HashMap<>();

    public static DataSource getDataSource(final String sourceName) {

        return cache.computeIfAbsent(sourceName,
                (s) -> {
                    Properties props = new Properties();

                    try {
                        props.load(DBController.class.getClassLoader().getResourceAsStream("app.properties"));
                    } catch (IOException e) {
                        Logger.getLogger(DBController.class.getName()).log(Level.SEVERE,
                                "Failed to load properties from app.properties", e);
                        throw new RuntimeException("Failed to load properties from app.properties", e);
                    }

                    PGSimpleDataSource source = new PGSimpleDataSource();

                    String prefix = sourceName + ".";

                    String[] serverNames = { props.getProperty(prefix + "dbhost") };

                    source.setServerNames(serverNames);
                    source.setUser(props.getProperty(prefix + "username"));
                    source.setDatabaseName(props.getProperty(prefix + "dbname"));
                    source.setPassword(props.getProperty(prefix + "password"));
                    source.setCurrentSchema(props.getProperty(prefix + "schema"));

                    return source;
                });
    }

    static Properties properties(String propFileName) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(propFileName);) {
            properties.load(fis);
        } catch (IOException ignored) {
            Logger.getLogger(DBController.class.getName()).log(
                    Level.INFO,
                    "attempt to read file failed'",
                    ignored);
        }
        return properties;
    }
}
