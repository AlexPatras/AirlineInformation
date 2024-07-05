package businessLogic;

import java.util.ArrayList;
import java.util.regex.Pattern;

import dataRecords.StartSalesData;
import persistence.StartSalesDAO;
import persistence.StartSalesPersistenceException;
import persistence.DAO;
import persistence.FlightNonDAO;
import persistence.FlightNonDAOImpl;
import persistence.FlightPersistenceException;
import persistence.InputValidationException;
import persistence.PassengerNonDAO;
import persistence.PassengerNonDAOImpl;
import persistence.PassengerPersistenceException;
import persistence.PlaneNonDAO;
import persistence.PlaneNonDAOImpl;
import persistence.PlanePersistenceException;

public class StartSalesImpl implements StartSalesManager {

	private DAO<StartSalesData> startSalesDAO = new StartSalesDAO();
	private FlightNonDAO flightNonDAO = new FlightNonDAOImpl();

	@Override
	public void create(StartSalesData startSalesData)
			throws StartSalesBusinessException, InvalidInputBusinessException {

		if (startSalesData.flightID() == 0) {
			throw new InvalidInputBusinessException("Provide the flight id.");
		} else {
			try {
				startSalesDAO.create(startSalesData);
			} catch (Exception e) {
				throw new StartSalesBusinessException("BL: Couldn't create booking.", e);
			}
		}

	}

	@Override
	public void setStartSalesDAO(StartSalesDAO StartSalesDAO) {
		this.startSalesDAO = startSalesDAO;
	}

	@Override
	public void setFlightNonDao(FlightNonDAO flightNonDAO) {
		this.flightNonDAO = flightNonDAO;
	}

    @Override
    public String getArrivalIATACode(int flightId) throws FlightBusinessException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}