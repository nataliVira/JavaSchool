package connect.kafka.connect;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class CustomDatabaseSinkTask extends SinkTask {

    private static final Logger log = LoggerFactory.getLogger(CustomDatabaseSinkTask.class);

    private Connection connection;
    private String topic;
    private String tableName;
    private PreparedStatement stmnt;

    @Override
    public String version() {
        return new CustomDbStreamSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig config = new AbstractConfig(CustomDbStreamSinkConnector.CONFIG_DEF, props);
        try {
            String url = config.getString(CustomDbStreamSinkConnector.CONNECTION_URL);
            String user = config.getString(CustomDbStreamSinkConnector.USERNAME);
            String pass = config.getString(CustomDbStreamSinkConnector.PASSWORD);
            this.tableName = config.getString(CustomDbStreamSinkConnector.TABLE_NAME);
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, user, pass);
            this.connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't connect with database for FileStreamSinkTask", e);
        }
    }

    @Override
    public void put(Collection<SinkRecord> sinkRecords) {
        try {
            this.stmnt = connection.prepareStatement("insert into transaction (key, value) \n" +
                    "  values (?, ?)");
            for (SinkRecord record : sinkRecords) {
                log.trace("Writing line to {}: {}", tableName, record.value());
                String key = record.topic() + record.kafkaPartition() + record.kafkaOffset();
                stmnt.setString(1, key);
                stmnt.setString(2, record.value().toString());
                stmnt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Couldn't execute PreparedStatement for FileStreamSinkTask", e);
        }
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {
        log.trace("Flushing to database");
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            if (this.stmnt != null) {
                this.stmnt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
