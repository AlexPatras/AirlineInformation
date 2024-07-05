package businessLogic;

import java.time.LocalDate;
import java.util.List;

public interface APIClient {
    public double fetchWeather(String latitude, String longitude, LocalDate date, String discountMode) throws DiscountNotAppliedException;

    public List<Double> getCoordinates(String location) throws DiscountNotAppliedException;
}
