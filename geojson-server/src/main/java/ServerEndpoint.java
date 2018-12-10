import org.json.simple.parser.JSONParser;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@Path("/")
public class ServerEndpoint {


    @GET
    @Path("/geodata")
    public Response readGeoJSON() {
        try {
            JSONParser parser = new JSONParser();

            Object data = parser.parse(new FileReader(Processor.getUserDirectory()+"/geomap-defensie/json/geodata.geojson"));//path to the JSON file.

            String json = data.toString();
            System.out.println("got request");
            return response(json);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @GET
    @Path("/")
    @Produces({MediaType.TEXT_HTML})
    public FileInputStream homeMap(){
        System.out.println("New request map");
        try {
            File index = new File(Processor.getUserDirectory()+"/geomap-defensie/web/index.html");
            return new FileInputStream(index);
        }
        catch (FileNotFoundException e){

            e.printStackTrace();
        }
        return null;
    }


    @GET
    @Path("/index.js")
    public Response jsCode(){
        System.out.println("New request code");
        try {
            String content = new String(Files.readAllBytes(Paths.get(Processor.getUserDirectory()+"/geomap-defensie/web/index.js")));
            return response(content);
        }
        catch (Exception e){

            e.printStackTrace();
        }
        return null;
    }



    public Response response(String text) {
        return Response.ok() //200
                .entity(text)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                .header("Access-Control-Allow-Headers", "Access-Control-Allow-Methods, Access-Control-Allow-Origin, Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers")
                .header("Access-Control-Request-Headers", "Access-Control-Allow-Origin, Content-Type")
                .allow("OPTIONS").build();
    }

}

