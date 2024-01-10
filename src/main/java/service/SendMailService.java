package service;

import connection.ConfigManagement;
import connection.ConnectorManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.HandleLogicUtils;
import utils.SendUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;
import java.util.Set;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class SendMailService {
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Logger logger = LogManager.getLogger(SendMailService.class);

    static Properties config = ConfigManagement.getConfig();

    public static boolean sendEmail(Set<String> tos, Set<String> ccs, Set<String> bccs, Set<File> files, String subject, String mainBody, String emailFrom) {

        try {
            Socket server = ConnectorManager.connectToServer(config.getProperty("smtp.host"), Integer.parseInt(config.getProperty("smtp.port")));
            if (server == null) {
                System.out.println("Socket is not connect, please check you host or port!");
                return false;
            }
            String dateTime = HandleLogicUtils.getDateTime();
            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            String response = reader.readLine();
            System.out.println(response);
            // Send HELO command
            SendUtils.responseCommand(writer, "EHLO ", reader);
            SendUtils.responseCommand(writer, "MAIL FROM:<" + emailFrom + ">", reader);
            // Send RCPT TO command
            rcptTo(tos, ccs, bccs);
            // Send DATA command
            SendUtils.sendCommandData(writer, "DATA");
            SendUtils.sendCommandData(writer, "Date: " + dateTime);

            //headers
            SendUtils.sendCommandData(writer, "From: " + emailFrom);
            sendTo(tos, ccs, bccs);
            formatBody(subject, mainBody);
            //attached file
            if (!CollectionUtils.isEmpty(files)) {
                files.forEach(x -> {
                    try {
                        attachFile(writer, x.getAbsolutePath(), x.getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            //finish
            endMail();
            server.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void attachFile(BufferedWriter writer, String filePath, String fileName) throws IOException {
        // Read the file content and encode it in Base64
        Path path = Paths.get(filePath);
        SendUtils.sendCommandData(writer, "--boundary_text");
        SendUtils.sendCommandData(writer, "Content-Type: application/octet-stream; name=\"" + fileName + "\"");
        SendUtils.sendCommandData(writer, "Content-Disposition: attachment; filename=\"" + fileName + "\"");
        SendUtils.sendCommandData(writer, "Content-Transfer-Encoding: base64");
        SendUtils.sendCommandData(writer, "");
        byte[] fileBytes = Files.readAllBytes(path);
        String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
        SendUtils.sendCommandBufferData(writer, base64Content);
        SendUtils.sendCommandData(writer, "");

    }

    private static void endMail() throws IOException {
        SendUtils.sendCommandData(writer, "--boundary_text--");
        SendUtils.sendCommandData(writer, ".");
        SendUtils.responseCommand(writer, "QUIT", reader);
    }


    private static void formatBody(String subject, String body2) throws IOException {
        SendUtils.sendCommandData(writer, "Subject: " + subject);
        SendUtils.sendCommandData(writer, "MIME-Version: 1.0");
        SendUtils.sendCommandData(writer, "Content-Type: multipart/mixed; boundary=boundary_text");
        SendUtils.sendCommandData(writer, "");
        //body
        SendUtils.sendCommandData(writer, "--boundary_text");
        SendUtils.sendCommandData(writer, "Content-Type: text/plain; charset=\"utf-8\"");
        SendUtils.sendCommandData(writer, "");
        SendUtils.sendCommandData(writer, body2);
    }

    private static void rcptTo(Set<String> tos, Set<String> ccs, Set<String> bccs) {
        tos.forEach(x -> {
            try {
                SendUtils.responseCommand(writer, "RCPT TO:<" + x + ">", reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if (!CollectionUtils.isEmpty(ccs)) {
            ccs.forEach(x -> {
                try {
                    SendUtils.responseCommand(writer, "RCPT TO:<" + x + ">", reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (!CollectionUtils.isEmpty(bccs)) {
            bccs.forEach(x -> {
                try {
                    SendUtils.responseCommand(writer, "RCPT TO:<" + x + ">", reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void sendTo(Set<String> tos, Set<String> ccs, Set<String> bccs) {
        tos.forEach(x -> {
            try {
                SendUtils.sendCommandData(writer, "To: " + x);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if (!CollectionUtils.isEmpty(ccs)) {
            ccs.forEach(x -> {
                try {
                    SendUtils.sendCommandData(writer, "Cc: " + x);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
