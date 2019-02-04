import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnection {
    private Connection connection = null;
    private Session session= null;

    private String host;
    private String servUser;
    private String servPwd;
    private int port;

    private String rhost;
    private int rport;
    private int lport;

    private String driverName;
    private String db2Url;
    private String dbUsr;
    private String dbPwd;



    public SQLConnection(){
        this.host = "104.40.143.12";
        this.servUser = "doug";
        this.servPwd = "Thinkpad123@";
        this.port = 22;

        this.rhost = "localhost";
        this.rport = 3306;
        this.lport = 3307;

        this.driverName = "com.mysql.cj.jdbc.Driver";
        this.db2Url = "jdbc:mysql://localhost:" + lport + "/logdb";
        this.dbUsr = "root";
        this.dbPwd = "defensie";
    }

    public void makeConnection(){
        try {
            System.out.println("Making connection with "+this.db2Url);
            makeSSHConnection();
            // Connect to remote database
            Class.forName(driverName);
            this.connection = DriverManager.getConnection(db2Url, dbUsr, dbPwd);
            System.out.println("Connection to database established!");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed making connection to database");
            makeConnection();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void makeSSHConnection(){
        try {
            JSch jsch = new JSch();

            this.session = jsch.getSession(servUser, host, port);
            this.session.setPassword(servPwd);
            java.util.Properties config = new java.util.Properties();

            config.put("StrictHostKeyChecking", "no");
            this.session.setConfig(config);

            this.session.connect();

            this.session.setPortForwardingL(lport, rhost, rport);
        }
        catch (Exception e){
            System.out.println("Failed ssh connection");
            e.printStackTrace();
        }

    }

    public Session getSession() {
        return session;
    }
}
