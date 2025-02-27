package connect.kafka.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Very simple sink connector that works with database.
 */
public class CustomDbStreamSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(CustomDbStreamSinkConnector.class);

    public static final String CONNECTION_URL = "datasource";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TABLE_NAME = "tablename";


    static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(USERNAME, Type.STRING, null, Importance.HIGH, "Username fo destination database.")
            .define(PASSWORD, Type.STRING, null, Importance.HIGH, "Password fro destination database.")
            .define(TABLE_NAME, Type.STRING, null, Importance.HIGH, "TableName for destination database.")
            .define(CONNECTION_URL, Type.STRING, "jdbc:postgresql://localhost:5432/postgres", Importance.HIGH, "Destination database.");
    private Map<String, String> props;
    public CustomDbStreamSinkConnector() {

    }


    @Override
    public void start(Map<String, String> map) {
        this.props = map;
        log.info("Starting file sink connector writing to {}", new AbstractConfig(CONFIG_DEF, props).getString(CONNECTION_URL));
    }

    @Override
    public Class<? extends Task> taskClass() {
        return CustomDatabaseSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();

        configs.add(props);

        return configs;
    }

    @Override
    public void stop() {
        // Nothing to do since CustomDbStreamSinkConnector has no background monitoring.
    }

    @Override
    public ConfigDef config() {
        log.info("=====> {}", CONFIG_DEF);
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
