package com.example.demo.migrations;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class TransformDateChangeset implements CustomTaskChange {

    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YY");

    @Override
    public void execute(final Database database) throws CustomChangeException {
        final JdbcConnection databaseConnection = (JdbcConnection) database.getConnection();
        try {
            final PreparedStatement preparedStatement = databaseConnection.prepareStatement("UPDATE ADV_CAMPAING_HISTORY SET creation_date = ? WHERE id=?");
            final ResultSet resultSet = databaseConnection.createStatement().executeQuery("SELECT id, daily from ADV_CAMPAING_HISTORY");
            while (resultSet.next()) {
                final long id = resultSet.getLong(1);
                final String daily = resultSet.getString(2);
                preparedStatement.setDate(1, new Date(sdf.parse(daily).getTime()));
                preparedStatement.setLong(2, id);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (final Exception e) {
            throw new CustomChangeException(e);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public void setFileOpener(final ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(final Database database) {
        return null;
    }
}
