package async;

import model.FileData;
import model.Messages;
import utils.SendUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class ExcuteMessages {

    public static List<String> messages = new ArrayList<>();
    public static List<String> idsFromMail = new ArrayList<>();

    public static List<Messages> getNewMessages(BufferedWriter writer, BufferedReader reader) throws IOException {
        List<Messages> newMessages = new ArrayList<>();
        Map<Integer, String> idMap = new HashMap<>();
        messages.forEach((String message) -> {
            String[] temp = message.split("\\s+", 2);
            if (temp.length == 2) {
                idMap.put(Integer.parseInt(temp[0]), temp[1]);
            }
        });
        for (Map.Entry<Integer, String> entry : idMap.entrySet()) {
            newMessages.add(getMessage(entry.getKey(), writer, reader, entry.getValue()));
        }
        return newMessages;
    }


    protected static Messages getMessage(int i, BufferedWriter writer, BufferedReader reader, String messageId) throws IOException {
        String response = SendUtils.sendAndReturnForAsync(writer, "RETR " + i, reader);
        Map<String, List<String>> headers = new HashMap<>();
        String headerName;
        while (!(response = readResponseLine(reader)).isEmpty()) {
            if (response.startsWith("\t")) {
                continue; //no process of multiline headers
            }
            int colonPosition = response.indexOf(":");
            if (colonPosition < 0) {
                colonPosition = 0;
            }

            headerName = response.substring(0, colonPosition);
            String headerValue;
            if (headerName.length() >= colonPosition) {
                try {
                    headerValue = response.substring(colonPosition + 2);
                } catch (StringIndexOutOfBoundsException e) {
                    headerValue = response.substring(colonPosition);
                }
            } else {
                headerValue = "";
            }
            List<String> headerValues = headers.get(headerName);
            if (headerValues == null) {
                headerValues = new ArrayList<String>();
                headers.put(headerName, headerValues);
            }
            headerValues.add(headerValue);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        while (!(response = readResponseLine(reader)).equals(".")) {
            bodyBuilder.append(response + "\n");
        }
        return new Messages(headers, extractPlainText(bodyBuilder.toString()), extractFilenamesAndBase64(bodyBuilder.toString()), false, messageId);
    }

    private static String extractPlainText(String input) {
        // Define the regular expression pattern
        String patternString = "Content-Type: text/plain; charset=\"utf-8\"\\s*\\n\\n(.*?)(?=(--boundary_text|$))";

        // Compile the pattern
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);

        // Create a matcher
        Matcher matcher = pattern.matcher(input);

        // Find the first match
        if (matcher.find()) {
            // Extract the base64-encoded content
            return matcher.group(1).trim();
        } else {
            // Return an empty string if no match is found
            return "";
        }
    }

    private static List<FileData> extractFilenamesAndBase64(String input) {
        // Define the regular expression pattern to match filenames and base64 content
        String patternString = "filename=\"([^\"]+)\".*?base64\\s*\\n(.*?)\\n--boundary_text";
        // Specify the pattern to match multiple lines (including newlines) between filename and base64 content

        // Compile the pattern
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);

        // Create a matcher
        Matcher matcher = pattern.matcher(input);

        // Create a list to store the extracted data
        List<FileData> fileDataList = new ArrayList<>();

        // Find all matches
        while (matcher.find()) {
            // Extract filename and base64 content
            String filename = matcher.group(1);
            String base64Content = matcher.group(2);

            // Add the data to the list
            fileDataList.add(new FileData(filename, base64Content));
        }

        return fileDataList;
    }

    protected static String readResponseLine(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response.startsWith("-ERR"))
            throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
        return response;
    }
}
