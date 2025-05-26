package db.migration;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class V2__Import_facilities_data extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        // Get the Postgres CopyManager from the JDBC Connection:
        CopyManager copyManager = new CopyManager(
                context.getConnection()
                        .unwrap(BaseConnection.class)
        );

        // Load the CSV from the JAR’s classpath:
        try (InputStream is =
                     getClass().getResourceAsStream("/data/facilities.csv")) {
            if (is == null) {
                throw new IllegalStateException("Missing facilities.csv on classpath");
            }
            // Execute the COPY … FROM STDIN
            copyManager.copyIn(
                    "COPY facilities(col1, col2, col3) " +
                            "FROM STDIN WITH (FORMAT csv, HEADER true)",
                    new InputStreamReader(is)
            );
        }
    }
}
