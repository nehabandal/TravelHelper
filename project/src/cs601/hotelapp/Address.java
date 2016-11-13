package cs601.hotelapp;

public class Address {
	private String Street_Address=new String();
	private String City=new String();
	private String State=new String();
	private double Longitude,Latitude;
	
	public Address(String street_Address, String city, String state,
			double latitude, double longitude) {
		super();
		Street_Address = street_Address;
		City = city;
		State = state;
		Longitude = longitude;
		Latitude = latitude;
	}

	public Address() {
		super();
	}

	public String getStreet_Address() {
		return Street_Address;
	}

	public void setStreet_Address(String street_Address) {
		Street_Address = street_Address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	@Override
	public String toString() {
		return "Address [Street_Address=" + Street_Address + ", City=" + City
				+ ", State=" + State + ", Longitude=" + Longitude
				+ ", Latitude=" + Latitude + "]";
	}
	
	

}
