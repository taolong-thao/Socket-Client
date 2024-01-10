package utils;

import async.ExcuteMessages;
import connection.ConfigManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class SendUtils {
    private static final int CHARACTER_LIMIT = 15000;
    public static String account = "";
    static Properties config = ConfigManagement.getConfig();

    public static String readResponseLine(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response.startsWith("-ERR"))
            throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
        return response;
    }

    public static String responseCommand(BufferedWriter writer, String command, BufferedReader reader) throws IOException {
        System.out.println("Sending: " + command);
        writer.write(command + "\r\n");
        writer.flush();
        // Read and return server response
        return readResponseLine(reader);
    }

    public static void sendCommandData(BufferedWriter writer, String command) throws IOException {
        System.out.println("Sending: " + command);
        writer.write(command + "\r\n");
        writer.flush();
    }

    public static void sendCommandBufferData(BufferedWriter writer, String file) throws IOException {
        System.out.println("Sending: " + file);
        int chunkSize = CHARACTER_LIMIT;
        int totalChunks = (int) Math.ceil((double) file.length() / chunkSize);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min((i + 1) * chunkSize, file.length());
            String chunk = file.substring(start, end);
            writer.write(chunk +"\n");
            writer.flush();
        }
    }

    public static void sendForAsync(BufferedWriter writer, String command, BufferedReader reader) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        reader.readLine();
    }

    public static String sendAndReturnForAsync(BufferedWriter writer, String command, BufferedReader reader) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        return reader.readLine();
    }

    public static boolean sendAndReturnForAsyncUIDL(BufferedWriter writer, String command, BufferedReader reader) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        String line;
        List<String> temp = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("+OK")) {
                if (!line.startsWith("+OK") && !line.equals(".")) {
                    String substring = line.substring(0, line.lastIndexOf('.'));
                    temp.add(substring);
                    if(ExcuteMessages.idsFromMail.stream().noneMatch(substring::contains)){
                        ExcuteMessages.messages.add(substring);
                    }
                }
                if (line.equals(".")) {
                    writer.write("QUIT" + "\r\n");
                    writer.flush();
                    if (!ExcuteMessages.messages.isEmpty()) {
                        return true;
                    }
                }
                if(config.getProperty("app.restart").equals("1")){
                    ExcuteMessages.messages = temp;
                }
            }
        }
        return false;
    }
}
