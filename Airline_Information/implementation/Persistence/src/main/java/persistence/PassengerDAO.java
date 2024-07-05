package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.spi.DirStateFactory.Result;
import javax.sql.DataSource;

import dataRecords.PassengerData;

public class PassengerDAO implements DAO<PassengerData> {

    private DataSource db = DBController.getDataSource("db");
    private int generatedPassengerId;

    public PassengerDAO() {
        this.generatedPassengerId = -1;
    }

    @Override
    public void create(PassengerData entity) throws PassengerPersistenceException {

        ResultSet resultSet = null;

        if (entity.firstName().isEmpty()) {
            throw new IllegalArgumentException("The first name is required.");
        }
        if (entity.lastName().isEmpty()) {
            throw new IllegalArgumentException("The last name is required.");
        }
        if (entity.email().isEmpty()) {
            throw new IllegalArgumentException("The email is required.");
        }

        try {
            Connection conn = db.getConnection();
            String sql = "INSERT INTO passenger(firstname, lastname, email, phonenumber, nationality, docnumber, birthday) VALUES(?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement stm = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, entity.firstName());
            stm.setString(2, entity.lastName());
            stm.setString(3, entity.email());
            stm.setString(4, entity.phoneNumber());
            stm.setString(5, entity.nationality());
            stm.setString(6, entity.docNumber());
            stm.setString(7, entity.birthDay());
            stm.executeUpdate();
            resultSet = stm.getGeneratedKeys();

            if (resultSet.next()) {
                this.generatedPassengerId = resultSet.getInt(1);
            }

            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new PassengerPersistenceException("Couldn't add passenger to DB.", e);
        }
    }

    @Override
    public PassengerData read(int id) throws PassengerPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT * FROM passenger WHERE passengerid = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int passengerId = rs.getInt("passengerid");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phonenumber");
                String nationality = rs.getString("nationality");
                String docNumber = rs.getString("docnumber");
                String birthDay = rs.getString("birthday");

                PassengerData passengerData = new PassengerData(firstName, lastName, email, passengerId, phoneNumber,
                        nationality, docNumber, birthDay);
                return passengerData;
            }

            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new PassengerPersistenceException("Couldn't get passenger from DB.", e);
        }

        return null;
    }

    @Override
    public void update(PassengerData entity) throws PassengerPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "UPDATE passenger SET email = ?, phonenumber = ? WHERE passengerid = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, entity.email());
            stm.setString(2, entity.phoneNumber());
            stm.setInt(3, entity.passengerId());
            stm.executeUpdate();

            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new PassengerPersistenceException("Couldn't update passenger info in the DB.", e);
        }
    }

    @Override
    public void delete(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
