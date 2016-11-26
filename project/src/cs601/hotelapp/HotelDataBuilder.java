package cs601.hotelapp;

//import com.oracle.javafx.jmx.json.JSONException;
//import com.oracle.tools.packager.Log;

import cs601.concurrent.WorkQueue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HotelDataBuilder {
    private static final Logger log = Logger.getLogger(HotelDataBuilder.class.getName());
    private ThreadSafeHotelData threadSafehd = new ThreadSafeHotelData();
    private WorkQueue workqueue = new WorkQueue();
    private volatile int noOfFilesProcessed = 0;
    private int totalNoOfFiles = 0;


    //Enter starting line here
    private int startline = 1;
    //Enter number of lines here.
    private int numlines = 12;


    public HotelDataBuilder(ThreadSafeHotelData data) {
        threadSafehd = data;
    }

    public HotelDataBuilder(ThreadSafeHotelData data, WorkQueue q) {
        threadSafehd = data;
        this.workqueue = q;
    }


    /**
     * Read the json file with information about the hotels (id, name, address,
     * etc) and load it into the appropriate data structure(s). Note: This
     * method does not load reviews
     *
     * @param jsonFilename the name of the json file that contains information about the
     *                     hotels
     */

    public void loadHotelInfo(String jsonFilename) {

        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(new FileReader(jsonFilename));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray jsonArray = (JSONArray) jsonObject.get("sr");

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonobject = (JSONObject) jsonArray.get(i);

                String Hotel_Id = (String) jsonobject.get("id");
                String hotelName = (String) jsonobject.get("f");
                String Street_Address = (String) jsonobject.get("ad");
                String City = (String) jsonobject.get("ci");
                String State = (String) jsonobject.get("pr");
                String country = (String) jsonobject.get("c");
                JSONObject jsonobject1 = (JSONObject) jsonobject.get("ll");
                String Hotel_lat = (String) jsonobject1.get("lat");
                double lat = Double.parseDouble(Hotel_lat);
                String Hotel_lng = (String) jsonobject1.get("lng");
                double lan = Double.parseDouble(Hotel_lng);

                threadSafehd.addHotel(Hotel_Id, hotelName, City, State, Street_Address, lat, lan,country);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Load reviews for all the hotels into the appropriate data structure(s).
     * Traverse a given directory recursively to find all the json file with
     * reviews and load reviews from each json. Note: this method must be
     * recursive and use DirectoryStream as discussed in class.
     *
     * @param path the path to the directory that contains json file with
     *             reviews Note that the directory can contain json file, as
     *             well as subfolders (of subfolders etc..) with more json file
     */

    public void loadReviews(Path path) {

        List<Path> paths = new ArrayList<>();
        threadSafehd.getReviewFileList(path, paths);

        for (Path p : paths) {
            threadSafehd.collectReviewData(p);
        }
    }

    /**
     * Method to shutdown all threads
     */

    public synchronized void shutdown() {
        workqueue.shutdown();
        workqueue.awaitTermination();
    }

    /**
     * printToFile(String filename) that waits until all the runnable tasks have
     * finished executing, and calls printToFile method of class
     * ThreadSafeHotelData.
     *
     * @param filename
     */
    public void printToFile(Path filename) {
        while (noOfFilesProcessed < totalNoOfFiles) {
            try {
                Thread.sleep(100);
//                System.out.println(String.format("Processed %d of %d", noOfFilesProcessed, totalNoOfFiles));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadSafehd.printToFile(filename);
    }

    /**
     * open an SSLSocket to "talk to" the maps.googleapis.com webserver,
     * • prepare an HTTP GET request to the Places API Text Search Service. Use
     * version 1.1 of the HTTP protocol. Specify json as the output type. The link I
     * listed above describes the format of the request for the text search service. Here is
     * a sample request:
     * • send the request to the webserver via the output stream of the socket,
     * • receive the response; remove the headers
     * • parse the json you got in the response and add each attraction to the appropriate data structures in ThreadSafeHotelData (see below).
     * • close the streams and the socket.
     **/

    public synchronized void fetchAttractions(int radiusInMiles) {


        String API_KEY = "AIzaSyCWbsgJAmVlBpDfAsrrXdqJopuaDXgRBqQ";
        int radiusMet = radiusInMiles * 1609;
        for (String hotelId : threadSafehd.getHotels()) {
            String query = threadSafehd.generateQuery(hotelId);
            String urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query + "&radius=" + radiusMet + "&key=" + API_KEY;
            URL url;
            PrintWriter out = null;
            BufferedReader in = null;
            SSLSocket socket = null;
            try {
                url = new URL(urlString);

                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

                // HTTPS uses port 443
                socket = (SSLSocket) factory.createSocket(url.getHost(), 443);

                // output stream for the secure socket
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
                System.out.println("Request: " + request);

                out.println(request); // send a request to the server
                out.flush();

                // input stream for the secure socket.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // use input stream to read server's response
                String line;
                int linenumber = 1;
                StringBuffer sb = new StringBuffer();
                while ((line = in.readLine()) != null) {

                    if (linenumber < startline || linenumber >= startline + numlines) {
                        sb.append(line + "\n");
                        System.out.println(line);
                    }
                    linenumber++;
                }

                parseAttractions(hotelId, sb.toString());

                //workqueue.execute(new AttractionLoader(hotelId, sb.toString()));

            } catch (IOException e) {
                System.out.println(
                        "An IOException occured while writing to the socket stream or reading from the stream: " + e);
            } finally {
                try {
                    // close the streams and the socket
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException e) {
                    System.out.println("An exception occured while trying to close the streams or the socket: " + e);
                }


            }


        }
    }

    public void parseAttractions(String hotelId, String json) {

        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(json);

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray jsonArray = (JSONArray) jsonObject.get("results");

            for (int i = 0; i < jsonArray.size(); i++) {

                JSONObject jsonobject = (JSONObject) jsonArray.get(i);

                String attractionName = (String) jsonobject.get("name");
                String attractionId = (String) jsonobject.get("id");
                String attractionAddress = (String) jsonobject.get("formatted_address");


                double attractionRating = 0;
                if (jsonobject.containsKey("rating")) {
                    attractionRating = ((Number) jsonobject.get("rating")).doubleValue();
                }


//                  attractions.add(new TouristAttraction(attractionId,attractionName,attractionAddress,attractionRating));
                threadSafehd.addAttraction(attractionId, attractionName, attractionRating, attractionAddress, hotelId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error on parse data in jsonparser.java");
        }

    }

    /**
     * Takes a host and a string containing path/resource/query and creates a
     * string of the HTTP GET request
     *
     * @param host
     * @param pathResourceQuery
     * @return
     */
    private static String getRequest(String host, String pathResourceQuery) {

        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;

    }


    public static void main(String[] args) {

            ThreadSafeHotelData tdsafe=new ThreadSafeHotelData();
            WorkQueue wq=new WorkQueue();
            HotelDataBuilder data = new HotelDataBuilder(tdsafe,wq);

            // Load hotel info from hotels200.json
            data.loadHotelInfo("input/hotels200.json");
            data.fetchAttractions(2);
        tdsafe.printAttractionsNearEachHotel(Paths.get("attractionoutputnew"));
         //   data.loadReviews(Paths.get("input/reviews"));
            // Traverse input/reviews directory recursively,
            // find all the json files and load reviews
           // data.loadReviews(Paths.get("input/reviews"));
            //data.printToFile(Paths.get("outputFile_Neha"));
        }



    /*public static void main2(String[] args) {
        ThreadSafeHotelData tdsafe = new ThreadSafeHotelData();
        WorkQueue wq = new WorkQueue();
        HotelDataBuilder data = new HotelDataBuilder(tdsafe, wq);

        // Load hotel info from hotels200.json
        data.loadHotelInfo("input/hotels200.json");

        // Traverse input/reviews directory recursively,
        // find all the json file and load reviews
        data.loadReviews(Paths.get("input/reviews8000"));
        data.printToFile(Paths.get("outputFile_Neha"));
        data.shutdown();
    }*/


}
