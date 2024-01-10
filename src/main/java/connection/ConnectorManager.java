package connection;

import utils.SendUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class ConnectorManager {
    private static Socket server = null;
    static Properties config = ConfigManagement.getConfig();

    public static Connection connection() {
        try {
            System.out.printf("connected...\n");
            return DriverManager.getConnection(config.getProperty("db.url"),
                    config.getProperty("db.user"),
                    config.getProperty("db.password"));
        } catch (SQLException e) {
            System.out.printf("connected...Error");
            e.printStackTrace();
        }
        return null;
    }

    public static Socket connectToServer(String host, int port) {
        try {
            server = new Socket(host, port);
            return server;
        } catch (IOException e) {
            return null;
        }
    }

    public static Socket startTls(Socket socket) throws IOException {
        // Upgrade to TLS
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        SendUtils.responseCommand(writer, "STARTTLS", reader);

        // Upgrade to TLS using SSLSocketFactory
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        return sslSocketFactory.createSocket(socket, socket.getInetAddress().getHostName(), socket.getPort(), true);
    }

    public static Socket startSSL() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket server = sslSocketFactory.createSocket(config.getProperty("pop3.host"), Integer.parseInt(config.getProperty("pop3.port")));

        // Perform SSL/TLS handshake
        ((SSLSocket) server).startHandshake();
        return server;
    }
}
