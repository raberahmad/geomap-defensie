import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Processor extends Thread {
    private Server server;
    private SQLConnection connectionDB;
    private List<String[]> results ;

    public Processor(){
        this.connectionDB = new SQLConnection();
        connectionDB.makeConnection();

        this.server = new Server();
        this.server.start();
    }

    public void run(){
        while (true){

            setResults(null);
            setResults(retrieveUsers());

            buildGeoJSON();

            //sleep(1000*60*20); //sleep for 1000 milisec * 60 * 20 = 20 minutes

        }
    }

    public void buildGeoJSON(){
        JsonArrayBuilder featuresArray = Json.createArrayBuilder();

        for(int i = 0; i<results.size(); i++){

            System.out.println("userid:\t"+results.get(i)[0]+", naam:\t"+results.get(i)[1]+", ip:\t"+results.get(i)[2]+", date:\t"+results.get(i)[3]+", app:\t"+results.get(i)[4]);
            ExtremeIPLookup extremeIPLookup= new ExtremeIPLookup(results.get(i)[2]); //ip gegevens opvragen
            System.out.println(" ");


            JsonObjectBuilder features = Json.createObjectBuilder();

            features.add("type", "Feature").
                    add("geometry", Json.createObjectBuilder().add("type","Point")
                            .add("coordinates", Json.createArrayBuilder().add(extremeIPLookup.getLon()).add(extremeIPLookup.getLat()))).
                    add("properties", Json.createObjectBuilder().
                            add("userID", results.get(i)[0]));


            int index = i+1;
            int totalIndex= results.size()+1;
            featuresArray.add(features.build());
            System.out.println("Number "+index+" of "+totalIndex+" has been processed.");
            sleep(1300);
        }

        JsonObjectBuilder geojson = Json.createObjectBuilder();
        geojson.add("type", "FeatureCollection")
                .add("features", featuresArray.build());


        JsonObject json = geojson.build();
        String data = json.toString();
        System.out.println(data);

        writeGEOJSONtoFILE(data);


    }

    public void writeGEOJSONtoFILE(String geodata){
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/geodata.geojson");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(geodata);
            printWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
            writeGEOJSONtoFILE(geodata);
        }
    }

    public List<String[]> retrieveUsers(){ // SQL statement for retrieving every user that's online
        List<String[]> dbResults = new ArrayList<String[]>();

        try {
            //Gebruik in de Pstmt ASC of DESC om de eerste 5 of de laatste 5 recods optevragen.
            PreparedStatement ps = connectionDB.getConnection().prepareStatement("select userid, naam, ip, date, app from user, login where user.userid = login.user_userid and session = 1 order by login.date limit 50");

            ResultSet rs = ps.executeQuery();



            String[] tempArray ;
            while (rs.next()){
                tempArray = null;
                tempArray = new String[5];

                tempArray[0] = Integer.toString(rs.getInt("userid"));
                tempArray[1] = rs.getString("naam");
                tempArray[2] = rs.getString("ip");
                tempArray[3] = rs.getTimestamp("date").toString();
                tempArray[4] = rs.getString("app");

                dbResults.add(tempArray);
            }
        }
        catch (SQLException e){
            System.out.println("Failed retrieving users from database");
            e.printStackTrace();
        }

        return dbResults;
    }

    public void setResults(List<String[]> results) {
        this.results = results;
    }

    public void sleep(int time){

        long inSeconds = time/1000;
        try {
            System.out.println("Thread will sleep for "+Long.toString(inSeconds)+" seconds.");
            Thread.sleep(time);
        }
        catch (InterruptedException e){
            System.out.println("Thread was interupted");
            e.printStackTrace();
        }
    }
}
