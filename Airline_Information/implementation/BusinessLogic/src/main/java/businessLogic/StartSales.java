
package businessLogic;

import dataRecords.StartSalesData;

public class StartSales {

	StartSalesData startSalesData;

	public StartSales(StartSalesData startSalesData) {
		this.startSalesData = startSalesData;
	}

	public int getFlightID() {
		return this.startSalesData.flightID();
	}

}