import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.URI;

public class Server extends Thread {
    private int port = 1299;
    private ResourceConfig config;
    private HttpServer server;



    public Server(){

        URI uri = URI.create("http://104.40.143.12"+ port);

        this.config = new ResourceConfig(ServerEndpoint.class);
        config.register(JacksonJaxbJsonProvider.class);
        this.server = GrizzlyHttpServerFactory.createHttpServer(uri, config, true);



    }

    public void run(){
        try {
            server.start();
            System.out.println("server is gestart op port "+port);
            while (true){
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server is niet gestart");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Thread is interupted");
        }
        catch (ProcessingException e){
            e.printStackTrace();

        }
    }

}
