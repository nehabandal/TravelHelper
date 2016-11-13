package cs601.hotelapp;

public class Hotel implements Comparable<Hotel>
{
	private String id;
	private String hotelName;
	private Address address = new Address();
	
	public Hotel() {
		super();
	}
	public Hotel(String id, String hotel_Name) {
		super();
		this.id = id;
		hotelName = hotel_Name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	@Override
	public int compareTo(Hotel h) {
		
		if (getHotelName() == null || h.getHotelName() == null)
		      return 0;
	    return h.getHotelName().compareTo(getHotelName());
		
	}
	
	public Hotel(String id, String hotel_Name, Address address) {
		super();
		this.id = id;
		hotelName = hotel_Name;
		this.address = address;
	}

	@Override
	public String toString() {
		return "Hotel [id=" + id + ", hotelName=" + hotelName
				+ ", address=" + address + "]";
	}

	public Address getAddress() {
		return address;
	}
}
