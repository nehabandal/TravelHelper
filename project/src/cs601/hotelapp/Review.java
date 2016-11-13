package cs601.hotelapp;

import java.util.Date;

public class Review implements Comparable<Review> {

	private String Review_Id;
	private String Hotel_Id;
	private String Review_Title, Review_Text, Username;
	private Date date;
	private int Overall_Rating;

	public Review(String review_Id, String hotel_Id, String review_Title,
			String review_Text, String username, Date date, int overall_Rating) {
		super();
		Review_Id = review_Id;
		Hotel_Id = hotel_Id;
		Review_Title = review_Title;
		Review_Text = review_Text;
		Username = username;
		this.date = date;
		Overall_Rating = overall_Rating;
	}

	public String getReview_Id() {
		return Review_Id;
	}

	public void setReview_Id(String review_Id) {
		Review_Id = review_Id;
	}

	public String getHotel_Id() {
		return Hotel_Id;
	}

	public void setHotel_Id(String hotel_Id) {
		Hotel_Id = hotel_Id;
	}

	public String getReview_Title() {
		return Review_Title;
	}

	public void setReview_Title(String review_Title) {
		Review_Title = review_Title;
	}

	public String getReview_Text() {
		return Review_Text;
	}

	public void setReview_Text(String review_Text) {
		Review_Text = review_Text;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getOverall_Rating() {
		return Overall_Rating;
	}

	public void setOverall_Rating(int overall_Rating) {
		Overall_Rating = overall_Rating;
	}

	@Override
	public int compareTo(Review r) {
		int result = this.getDate().compareTo(r.getDate());
		if (result != 0) {
			return result;
		}

		result = this.getUsername().compareTo(r.getUsername());
		if (result != 0) {
			return result;
		}
		return this.getReview_Id().compareTo(r.getReview_Id());
	}

	public String toString() {
		return ("[HotelID = " + Hotel_Id + "reviewID=" + Review_Id
				+ ", reviewTitle=" + Review_Title + ", reviewText="
				+ Review_Text + ", userName=" + Username + ", Review Date="
				+ date + " , ]\n\n\n");
	}

}
