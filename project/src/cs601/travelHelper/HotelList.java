package cs601.travelHelper;

/**
 * Created by npbandal on 11/16/16.
 */
public class HotelList {
    private String hotelName;
    private String address;
    private double rating;

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setAddress(String address) {
        this.address = address;

    }
}
