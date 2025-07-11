package com.dio.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public void executeMigration() {
        var originalOut = System.out;
        var originalErr = System.err;

        try (var fos = new FileOutputStream("liquibase.log")) {
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));

            try {
                var jdbcConnection = new JdbcConnection(connection);

                var liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection);

                liquibase.update();

            } catch (LiquibaseException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
