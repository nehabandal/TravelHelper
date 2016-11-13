package cs601.hotelapp;

import java.util.Comparator;

public class TouristAttraction implements Comparable<TouristAttraction> {
	private String attractionId;
	private String name;
	private String address;

	private double rating;

	public TouristAttraction(String attractionId, String name, String address, double rating) {
		super();
		this.attractionId = attractionId;
		this.name = name;
		this.address = address;
		this.rating = rating;

	}


	public String getAttractionId() {
		return attractionId;
	}

	public void setAttractionId(String attractionId) {
		this.attractionId = attractionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "TouristAttraction [attractionId=" + attractionId + ", name=" + name + ", address=" + address
				+ ", rating=" + rating + "]";
	}

	/*public int compareTo(TouristAttraction r) {

		return r.getAttractionId().compareTo(getAttractionId());

	}*/
	public int compareTo(TouristAttraction r)
	{
		/*int result = this.getAttractionId().compareTo(r.getAttractionId());
		if (result != 0) {
			return result;
		}*/

		return this.getName().compareTo(r.getName());

		//return result;
	}

}


