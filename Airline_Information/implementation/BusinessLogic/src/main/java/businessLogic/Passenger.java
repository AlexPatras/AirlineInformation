package businessLogic;

import dataRecords.PassengerData;

public class Passenger {
    private PassengerData passengerData;

    public Passenger(PassengerData passengerData) {
        this.passengerData = passengerData;
    }

    public String getFirstName() {
        return this.passengerData.firstName();
    }

    public String getLastName() {
        return this.passengerData.lastName();
    }

    public String getEmail() {
        return this.passengerData.email();
    }

    public int getPassengerId() {
        return this.passengerData.passengerId();
    }

    public String getPhoneNumber() {
        return this.passengerData.phoneNumber();
    }

    public String getNationality() {
        return this.passengerData.nationality();
    }

    public String getDocNumber() {
        return this.passengerData.docNumber();
    }

    public String getBirthDay() {
        return this.passengerData.birthDay();
    }

    public Passenger setPassengerId(int id) {
        return new Passenger(new PassengerData(getFirstName(), getLastName(), getEmail(), id, getPhoneNumber(),
                getNationality(), getDocNumber(), getBirthDay()));
    }

    public PassengerData getPassengerData() {
        return this.passengerData;
    }

    @Override
    public String toString() {
        return "First name: " + this.passengerData.firstName() + ", Last name: " + this.passengerData.lastName()
                + ", Email: " + this.passengerData.email() + ", ID: " + this.passengerData.passengerId()
                + ", Phone number: " + this.passengerData.phoneNumber() + ", Nationality: "
                + this.passengerData.nationality() + ", Document number: " + this.passengerData.birthDay();
    }
}
