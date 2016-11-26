package cs601.hotelapp;

import cs601.concurrent.ReentrantReadWriteLock;
import cs601.travelHelper.DatabaseHandler;
import cs601.travelHelper.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class ThreadSafeHotelData - a data structure that stores information about
 * hotels and hotel reviews. Allows to quickly lookup a hotel given the hotel
 * id. Allows to easily find hotel reviews for a given hotel, given the hotelID.
 * Reviews for a given hotel id are sorted by the date and user nickname.
 */

public class ThreadSafeHotelData {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Set<String> reviewIdsSeen = new HashSet<>();
    private Map<String, List<Review>> reviewCollection = new TreeMap<String, List<Review>>();
    private Map<String, Hotel> hotelIdTreeMap = new TreeMap<String, Hotel>();
    private volatile int noOfAttractionFilesProcessed = 0;
    private int totalNoOfAttractionFiles = 0;
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

    private Map<String, List<TouristAttraction>> attractionCollection = new TreeMap<String, List<TouristAttraction>>();
    private String attractions_hotel;
    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/search";

    private static final String OUT_JSON = "/json";

    // KEY!
    private static final String API_KEY = "YOUR KEY";

    /**
     * Default constructor.
     */

    public ThreadSafeHotelData() {
        super();
    }

    /**
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure(s).
     *
     * @param hotelId       - the id of the hotel
     * @param hotelName     - the name of the hotel
     * @param city          - the city where the hotel is located
     * @param state         - the state where the hotel is located.
     * @param streetAddress - the building number and the street
     * @param latitude
     * @param longitude
     */
    public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress,
                         double latitude, double longitude,String country) {
        lock.lockWrite();
        Address address = new Address(streetAddress, city, state, latitude, longitude);
        Hotel hotel = new Hotel(hotelId, hotelName, address);
//        Status status = dbhandler.addHotelDB(hotelId,hotelName,streetAddress,city,state,country);
//        if(status == Status.OK) { // registration was successful
//            System.out.println("row added");
//        }

        try {
            hotelIdTreeMap.put(hotelId, hotel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Add a new review.
     *
     * @param hotelId       - the id of the hotel reviewed
     * @param reviewId      - the id of the review
     * @param rating        - integer rating 1-5.
     * @param reviewTitle   - the title of the review
     * @param review        - text of the review
     * @param isRecommended - whether the user recommends it or not
     * @param date          - date of the review in the format yyyy-MM-dd, e.g.
     *                      2016-08-29.
     * @param username      - the nickname of the user writing the review.
     * @return true if successful, false if unsuccessful because of invalid date
     * or rating. Needs to catch and handle ParseException if the date
     * is invalid. Needs to check whether the rating is in the correct
     * range
     */
    public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
                             boolean isRecommended, String date, String username) {
        lock.lockWrite();
        try {
            if (reviewIdsSeen.contains(reviewId)) {
                return false;
            }
            reviewIdsSeen.add(reviewId);
        } finally {
            lock.unlockWrite();
        }
        Date dateObject = null;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateObject = simpleDateFormat.parse(date);
        } catch (java.text.ParseException e) {
            // e.printStackTrace();
            return false;
        }
        if (rating < 0 || rating > 5) {
            return false;
        }
        if (username == null || username.isEmpty()) {
            username = "anonymous";
        }
        Review reviewObject = new Review(reviewId, hotelId, reviewTitle, review, username, dateObject, rating);
        Status status = dbhandler.addReviewDB(reviewId, hotelId, reviewTitle, review, username, date, rating);
        if(status == Status.OK) { // registration was successful
            System.out.println("row added");
        }
        lock.lockWrite();
        try {
            List<Review> reviewList = reviewCollection.get(hotelId);
            if (reviewList == null) {
                reviewList = new ArrayList<>();
                reviewCollection.put(hotelId, reviewList);
            }
            reviewList.add(reviewObject);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlockWrite();
        }
        return true;
    }

    /**
     * Return an alphabetized list of the ids of all hotels
     *
     * @return
     */
    public List<String> getHotels() {
        return new ArrayList<>(hotelIdTreeMap.keySet());
    }

    public String generateQuery(String hotelId) {
        String s = null;
        Hotel hotel = hotelIdTreeMap.get(hotelId);
        Address add = hotel.getAddress();
        String city = add.getCity();
        double lat = add.getLatitude();
        double longi = add.getLongitude();
        String textCity = city.replaceAll("\\s", "%20");
        return s = "tourist%20attractions+in+" + textCity + "&location=" + lat + "," + longi;
    }

    public void getReviewFileList(Path directoryPath, List<Path> files) {
        File directory = new File(directoryPath.toUri());

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                files.add(file.toPath());
            } else if (file.isDirectory()) {
                getReviewFileList(file.toPath(), files);
            }
        }
    }

    public void collectReviewData(Path jsonpath) {
        // local thrdsaf object,
        JSONParser parser = new JSONParser();
        String stringJSONPath = jsonpath.toString();
        Object obj;
        try {
            obj = parser.parse(new FileReader(stringJSONPath));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject reviewDetails = (JSONObject) jsonObject.get("reviewDetails");
            JSONObject reviewCollectionJSON = (JSONObject) reviewDetails.get("reviewCollection");
            JSONArray review = (JSONArray) reviewCollectionJSON.get("review");
            for (int i = 0; i < review.size(); i++) {
                JSONObject reviewTextJSON = (JSONObject) review.get(i);
                String ReviewID_in = (String) reviewTextJSON.get("reviewId");
                String HotelID = (String) reviewTextJSON.get("hotelId");
                String ReviewText = (String) reviewTextJSON.get("reviewText");
                String Title = (String) reviewTextJSON.get("title");
                long Rating = (long) reviewTextJSON.get("ratingOverall");
                String IsRecom = (String) reviewTextJSON.get("isRecom");
                boolean IsRecoms = "YES".equals(IsRecom) ? true : false;
                String Uname = (String) reviewTextJSON.get("userNickname");
                String date = (String) reviewTextJSON.get("reviewSubmissionTime");
                //long AvgRating = (long) reviewTextJSON.get("avgOverallRating");

                double AvgRating = 0;
                if (reviewTextJSON.containsKey("avgOverallRating")) {
                    AvgRating = ((Number) reviewTextJSON.get("avgOverallRating")).doubleValue();
                }
                addReview(HotelID, ReviewID_in, (int) Rating, Title, ReviewText, IsRecoms, date, Uname);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /*public void mergeReviews(ThreadSafeHotelData threadSafeHotelData) {
        lock.lockWrite();
        try {
            reviewCollection.putAll(threadSafeHotelData.reviewCollection);
        } finally {
            lock.unlockWrite();
        }
    }*/

    /**
     * Returns a string representing information about the hotel with the given
     * id, including all the reviews for this hotel separated by
     * -------------------- Format of the string: HoteName: hotelId
     * streetAddress city, state -------------------- Review by username: rating
     * ReviewTitle ReviewText -------------------- Review by username: rating
     * ReviewTitle ReviewText ...
     *
     * @param hotel id
     * @return - output string.
     */
    public String toString(String hotelID) {

        Hotel hotel = hotelIdTreeMap.get(hotelID);
        lock.lockRead();

        if (hotel == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(hotel.getHotelName()).append(": ").append(hotel.getId()).append("\n");

        Address address = hotel.getAddress();
        sb.append(address.getStreet_Address()).append("\n");
        sb.append(address.getCity() + ", " + address.getState()).append("\n");
        try {

            List<Review> reviews = reviewCollection.get(hotelID);
            if (reviews != null) {
                Collections.sort(reviews);
                for (Review r1 : reviews) {
                    sb.append("--------------------\n");
                    String username = r1.getUsername();
                    sb.append("Review by " + username + ": " + r1.getOverall_Rating() + "\n");
                    // sb.append("Date: ").append(r1.getDate()).append("\n");
                    sb.append(r1.getReview_Title() + "\n");
                    sb.append(r1.getReview_Text() + "\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlockRead();
        }
        return sb.toString();
    }

    /**
     * printToFile(String filename) that waits until all the runnable tasks have
     * finished executing, and calls printToFile method of class
     * ThreadSafeHotelData.
     *
     * @param filename
     */
    public void printToFile(Path filename) {
        lock.lockRead();
        try {
            FileWriter writer = new FileWriter(filename.toFile());
            BufferedWriter out = new BufferedWriter(writer);

            out.newLine();
            for (Map.Entry<String, Hotel> e : hotelIdTreeMap.entrySet()) {
                out.write("********************\n");
                out.write(toString(e.getKey()));
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlockRead();
        }
    }

    /**
     * that creates a TouristAttraction object given the arguments of the method
     * and adds information about this attraction to the maps described above.
     *
     * @param attractionId
     * @param name
     * @param rating
     * @param address
     * @param hotelId
     */
    public void addAttraction(String attractionId, String name, double rating, String address, String hotelId) {

        TouristAttraction touristattraction = new TouristAttraction(attractionId, name, address, rating);
        Status status = dbhandler.addAttractionDB(attractionId,name,rating,address,hotelId);
        if(status == Status.OK) { // registration was successful
            System.out.println("row added");
        }

        ++noOfAttractionFilesProcessed;
        //ArrayList<TouristAttraction> resultList = null;

        try {

            List<TouristAttraction> attractionList = attractionCollection.get(hotelId);

            if (attractionList == null) {
                attractionList = new ArrayList<>();
                attractionCollection.put(hotelId, attractionList);
            }
            attractionList.add(touristattraction);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * returns a String representing attractions for a particular hotel in the
     * following format:
     *
     * @param hotelId
     * @return
     */
    public String getAttractions(String hotelId) {
        Hotel hotel = hotelIdTreeMap.get(hotelId);


        if (hotel == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Attractions near ");
        sb.append(hotel.getHotelName()).append(", ").append(hotel.getId()).append("\n");


        try {

            List<TouristAttraction> touristAttractions = attractionCollection.get(hotelId);
            if (touristAttractions != null) {

                //Collections.sort(touristAttractions);

                for (TouristAttraction t1 : touristAttractions) {
                    //sb.append("--------------------\n");
                    String attractionName = t1.getName();
                    sb.append(attractionName + "; " + t1.getAddress() + "\n");


                }
                sb.append("++++++++++++++++++++");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * prints information about attractions near each hotel. Calls
     * getAttractions for each hotel in the hotels map. Prints
     * ++++++++++++++++++++ after printing information for each hotel
     *
     * @param filename
     */
    public void printAttractionsNearEachHotel(Path filename) {

        try {

            FileWriter writer = new FileWriter(filename.toFile());
            BufferedWriter out = new BufferedWriter(writer);

            for (Map.Entry<String, List<TouristAttraction>> t : attractionCollection.entrySet()) {

                out.write(getAttractions(t.getKey()));
                out.newLine();

            }

            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
