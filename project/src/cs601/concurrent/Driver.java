package cs601.concurrent;
import cs601.hotelapp.HotelDataBuilder;
import cs601.hotelapp.ThreadSafeHotelData;
/**
 * Created by npbandal on 10/18/16.
 */
public class Driver {
    public static void main(String[] args) {
        ThreadSafeHotelData tdsafe = new ThreadSafeHotelData();

        HotelDataBuilder data = new HotelDataBuilder(tdsafe);
        data.loadHotelInfo("input/hotels200.json");
    }
}
