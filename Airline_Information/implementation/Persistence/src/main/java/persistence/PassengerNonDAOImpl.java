package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import dataRecords.PassengerData;

public class PassengerNonDAOImpl implements PassengerNonDAO {

    private DataSource db = DBController.getDataSource("db");

    private int nextFreeId;

    public PassengerNonDAOImpl() {
        this.nextFreeId = -1;
    }
    
    @Override
    public int getNextFreeId() throws PassengerPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT MAX(passengerid) from passenger";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            if (rs.next()) {
                this.nextFreeId = rs.getInt(1);
            }

            this.nextFreeId++;
            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new PassengerPersistenceException("P: Couldn't get the next free id from the DB.", e);
        }
        return this.nextFreeId;
    }
    
    @Override
    public PassengerData create(String firstName, String lastName, String email, String phoneNumber, String nationality,
            String docNumber, String birthDay) throws PassengerPersistenceException {
        PassengerData passengerData = new PassengerData(firstName, lastName, email, getNextFreeId(), phoneNumber,
                nationality, docNumber, birthDay);
        return passengerData;
    }

    @Override
    public int checkForExistingPassenger(PassengerData passengerData) throws PassengerPersistenceException {
        Connection conn;
        int recordsNumber = -1;

        try {
            conn = db.getConnection();
            String sql = "Select COUNT(*) as recordcount FROM passenger WHERE firstname = ? AND lastname = ? AND nationality = ? AND birthday = ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setString(1, passengerData.firstName());
            stm.setString(2, passengerData.lastName());
            stm.setString(3, passengerData.nationality());
            stm.setString(4, passengerData.birthDay());

            ResultSet rs = stm.executeQuery();

            recordsNumber = -1;

            if (rs.next()) {
                recordsNumber = rs.getInt("recordcount");
            }
            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new PassengerPersistenceException("P: Couldn't get existing passengers from the DB.", e);
        }

        int returnablePassengerID = 0;

        if (recordsNumber != 0) {
            try {
                conn = db.getConnection();
                String sql1 = "Select passengerid FROM passenger WHERE firstname = ? AND lastname = ? AND nationality = ? AND birthday = ?";
                PreparedStatement pstm = conn.prepareStatement(sql1);
                pstm.setString(1, passengerData.firstName());
                pstm.setString(2, passengerData.lastName());
                pstm.setString(3, passengerData.nationality());
                pstm.setString(4, passengerData.birthDay());

                ResultSet passengerID = pstm.executeQuery();

                if (passengerID.next()) {
                    returnablePassengerID = passengerID.getInt("passengerid");
                }
                pstm.close();
                conn.close();
            } catch (SQLException e) {
                throw new PassengerPersistenceException("P: Couldn't get existing passenger id from DB.", e);
            }
        }

        return returnablePassengerID;
    }
}
