import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.URI;

public class Server extends Thread {
    private int port = 1299;
    private ResourceConfig config;
    private HttpServer server;

    private static final String KEYSTORE_SERVER_FILE = "keys/keystore_server";
    private static final String KEYSTORE_SERVER_PWD = "key123";
    private static final String TRUSTORE_SERVER_FILE = "keys/truststore_server";
    private static final String TRUSTORE_SERVER_PWD = "key123";



    public Server(){

        // ssl config
        SSLContextConfigurator sslContext = new SSLContextConfigurator();

        // security
        sslContext.setKeyStoreFile(KEYSTORE_SERVER_FILE);
        sslContext.setKeyStorePass(KEYSTORE_SERVER_PWD);
        sslContext.setTrustStoreFile(TRUSTORE_SERVER_FILE);
        sslContext.setTrustStorePass(TRUSTORE_SERVER_PWD);


        URI uri = URI.create("https://104.40.143.12"+ port);

        this.config = new ResourceConfig(ServerEndpoint.class);
        config.register(JacksonJaxbJsonProvider.class);
        this.server = GrizzlyHttpServerFactory.createHttpServer(uri, config, true, new SSLEngineConfigurator(sslContext).setClientMode(false).setNeedClientAuth(false));



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
