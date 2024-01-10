package service;

import connection.ConfigManagement;
import connection.ConnectorManager;
import enums.Type;
import model.Account;
import model.Email;
import model.FileData;
import model.Messages;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import repository.EmailRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class MailService {
    static ConnectorManager connectorManager = new ConnectorManager();
    private static final EmailRepository repository = new EmailRepository(connectorManager);
    static Properties config = ConfigManagement.getConfig();

    public static Account handleLogin(String user, String password) {
        String convertPass = Base64.getEncoder().encodeToString(password.getBytes());
        if (user != null && password != null) {
            return repository.checkLogin(user, convertPass);
        }
        return null;
    }

    public static List<String> getAllByAccount(String accountId) {
        return repository.getAllByAccount(accountId);
    }

    public static List<Email> showAllMail(String accountId, String type) {
        List<Email> mails = repository.getAll(accountId, type);
        if (CollectionUtils.isEmpty(mails)) {
            return new ArrayList<>();
        }
        List<FileData> fileDataList = repository.getAllFileByEmailId(mails.stream().map(Email::getMailId).collect(Collectors.toList()));

        Map<String, List<FileData>> fileDataMap = fileDataList.stream()
                .collect(Collectors.groupingBy(FileData::getEmail_id));

        mails.forEach(mail -> {
            if (fileDataMap.containsKey(mail.getMailId())) {
                mail.getFileDataList().addAll(fileDataMap.get(mail.getMailId()));
            }
        });
        return mails;
    }

    public static void moveFolderMail(Type type, String mailId) {
        Email mailById = repository.getMailById(mailId);
        if (ObjectUtils.isEmpty(mailById)) {
            System.out.println("Mail not exists");
            return;
        }
        repository.moveTypeMail(type, mailId);
        System.out.println("Successfully!");
    }

    public static Email readMailById(String mailId) {
        Email mailById = repository.getMailById(mailId);
        List<FileData> fileDataList = repository.getAllFileByEmailId(Arrays.asList(mailId));
        repository.updateStateMail(mailId, 0);
        if (CollectionUtils.isNotEmpty(fileDataList)) {
            mailById.getFileDataList().addAll(fileDataList);
        }
        return mailById;
    }

    public static void makeStateEmail(String mailId, int state) {
        Email mailById = repository.getMailById(mailId);
        if (ObjectUtils.isEmpty(mailById)) {
            System.out.println("mail not exists");
            return;
        }
        repository.updateStateMail(mailId, state);
    }

    public static boolean checkExistById(String id) {
        return repository.checkById(id);
    }

    public static void saveEmail(Messages messages) {
        repository.save(messages);
    }

    private static void saveToFile(byte[] data, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, data);
    }

    public static void downloadFile(FileData x) {
        try {
            String base64 = x.getBase64Content().replaceAll("\\s", "");
            while (base64.length() % 4 != 0) {
                base64 += "=";
            }
            byte[] decodedBytes = Base64.getDecoder().decode(base64);

            // Specify the directory where you want to save the file
            String directory = config.getProperty("down.directory");
            saveToFile(decodedBytes, directory + x.getFilename());
            System.out.println(x.getFilename() + " File saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBase64(String fileId){
        String baseFile = repository.getBaseFile(fileId);
        if(baseFile!= null){
            return baseFile;
        }
        System.out.println("file not exists");
        return null;
    }
}
