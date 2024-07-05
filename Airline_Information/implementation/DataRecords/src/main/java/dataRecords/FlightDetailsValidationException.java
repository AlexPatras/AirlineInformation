/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataRecords;

/**
 *
 * @author patrasalexandru
 */
public class FlightDetailsValidationException extends Exception {

    public FlightDetailsValidationException(String message) {
        super(message);
    }

    public FlightDetailsValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
