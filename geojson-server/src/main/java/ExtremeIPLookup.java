
import org.json.JSONObject;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class ExtremeIPLookup {

    private String city;
    private String country;
    private String countryCode;
    private String IpType;
    private String lat;
    private String lon;
    private String query;

    public ExtremeIPLookup(String ip){
        try {
            String json = ClientBuilder.newClient().target("http://extreme-ip-lookup.com/json/"+ip).request().accept(MediaType.APPLICATION_JSON).get(String.class);
            JSONObject jsonObject = new JSONObject(json);

            this.city = jsonObject.getString("city");
            this.country = jsonObject.getString("country");
            this.countryCode = jsonObject.getString("countryCode");
            this.IpType = jsonObject.getString("ipType");
            this.lat = jsonObject.getString("lat");
            this.lon = jsonObject.getString("lon");
            this.query = jsonObject.getString("query");

            dataToString();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getIpType() {
        return IpType;
    }

    public String getQuery() {
        return query;
    }

    public void dataToString(){
        System.out.println("City:\t"+getCity());
        System.out.println("Country:\t"+getCountry());
        System.out.println("Code:\t"+getCountryCode());
        System.out.println("IP Type:\t"+getIpType());
        System.out.println("lat:\t"+getLat());
        System.out.println("lon:\t"+getLon());
        System.out.println("query:\t"+getQuery());
    }


}
