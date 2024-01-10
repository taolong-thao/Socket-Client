package repository;

import connection.ConfigManagement;
import connection.ConnectorManager;
import enums.Type;
import model.Email;
import model.FileData;
import model.Messages;
import org.apache.commons.collections4.CollectionUtils;
import model.Account;
import utils.HandleLogicUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import static utils.HandleLogicUtils.getHeaderValue;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class EmailRepository {
    private final Connection connection;
    static Properties config = ConfigManagement.getConfig();


    public EmailRepository(ConnectorManager connector) {
        this.connection = connector.connection();
    }

    public Account checkLogin(String user, String password) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Account WHERE email = ? and password = ?");
        ) {
            statement.setString(1, user);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return new Account(resultSet.getString(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllByAccount(String accountId){
        List<String> emails = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT email_id FROM Email where account_id = '" + accountId + "'");
            if(resultSet !=null){
                while (resultSet.next()) {
                    emails.add(resultSet.getString("email_id"));
                }
                return emails;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkById(String id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT email_id FROM Email WHERE email_id = ?");
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Email> getAll(String accountId, String type) {
        List<Email> emails = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Email where account_id = '" + accountId + "' and type_mail = '" + type + "'");
            while (resultSet.next()) {
                Email rs = new Email(resultSet.getString("email_id"), resultSet.getInt("is_new"),
                        resultSet.getString("body_mail"), resultSet.getString("from_mail"),
                        resultSet.getString("to_mail"), resultSet.getString("cc_mail"),
                        resultSet.getString("bcc_mail"), resultSet.getString("account_id"),
                        resultSet.getString("type_mail"),
                        resultSet.getString("subject_mail"));
                emails.add(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    public Email getMailById(String mailId) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Email where email_id = '" + mailId + "'");
            while (resultSet.next()) {
                return new Email(resultSet.getString("email_id"), resultSet.getInt("is_new"),
                        resultSet.getString("body_mail"), resultSet.getString("from_mail"),
                        resultSet.getString("to_mail"), resultSet.getString("cc_mail"),
                        resultSet.getString("bcc_mail"), resultSet.getString("account_id"),
                        resultSet.getString("type_mail"),
                        resultSet.getString("subject_mail"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStateMail(String mailId, int state) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE Email set is_new = '" + state + "' where email_id = '" + mailId + "'")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FileData> getAllFileByEmailId(List<String> mailIds) {
        List<FileData> files = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < mailIds.size(); i++) {
            builder.append("?,");
        }

        String placeHolders = builder.deleteCharAt(builder.length() - 1).toString();
        String stmt = "SELECT * FROM FileData WHERE email_id IN (" + placeHolders + ")";
        try (PreparedStatement preparedStatement = connection.prepareStatement(stmt)) {
            for (int i = 0; i < mailIds.size(); i++) {
                preparedStatement.setString(i + 1, mailIds.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                FileData rs = new FileData(resultSet.getString("file_id"),
                        resultSet.getString("file_name"), resultSet.getString("base64"),
                        resultSet.getString("email_id"));
                files.add(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return files;
    }

    public void save(Messages messages) {
        String regexIgnore = "[\\[\\]<>\"]";
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Email (email_id, is_new, body_mail, from_mail, to_mail, cc_mail, bcc_mail, account_id, type_mail, subject_mail) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            String from = getHeaderValue(messages, "From").replaceAll(regexIgnore, "");
            String subject = getHeaderValue(messages, "Subject").replaceAll(regexIgnore, "");
            String body = messages.getBody();
            handleMail(messages, statement, body, from, regexIgnore, subject);
            statement.executeUpdate();
            List<FileData> fileDatas = messages.getFileData();
            handleFile(messages, fileDatas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void moveTypeMail(Type type, String mailId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE Email set type_mail = '" + type.name() + "' where email_id = '" + mailId + "' ")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void handleMail(Messages messages, PreparedStatement statement, String body, String from, String regexIgnore, String subject) throws SQLException {
        statement.setString(1, messages.getMessageId());
        statement.setBoolean(2, true);
        statement.setString(3, body);
        statement.setString(4, from);
        statement.setString(5, getHeaderValue(messages, "To").replaceAll(regexIgnore, ""));
        statement.setString(6, getHeaderValue(messages, "Cc").replaceAll(regexIgnore, ""));
        statement.setString(7, getHeaderValue(messages, "Bcc").replaceAll(regexIgnore, ""));
        statement.setString(8, from);
        Set<String> filterFrom = HandleLogicUtils.getFilterConfig(config.getProperty("filter.from"));
        Set<String> filterSubject = HandleLogicUtils.getFilterConfig(config.getProperty("filter.subject"));
        Set<String> filterContent = HandleLogicUtils.getFilterConfig(config.getProperty("filter.content"));
        Set<String> filterSpam = HandleLogicUtils.getFilterConfig(config.getProperty("filter.spam"));
        if (CollectionUtils.isNotEmpty(filterFrom) && filterFrom.stream().anyMatch(from::contains)) {
            statement.setString(9, Type.PROJECT.name());
            System.out.println("have a new mail in folder: "+Type.PROJECT.name());
        } else if (CollectionUtils.isNotEmpty(filterSubject) && filterSubject.stream().anyMatch(subject::contains)) {
            statement.setString(9, Type.IMPORTANT.name());
            System.out.println("have a new mail in folder: "+Type.IMPORTANT.name());
        } else if (CollectionUtils.isNotEmpty(filterContent) && filterContent.stream().anyMatch(body::contains)) {
            statement.setString(9, Type.WORK.name());
            System.out.println("have a new mail in folder: "+Type.WORK.name());
        } else if (CollectionUtils.isNotEmpty(filterSpam) && (filterSpam.stream().anyMatch(body::contains) || filterSpam.stream().anyMatch(subject::contains))) {
            statement.setString(9, Type.SPAM.name());
            System.out.println("have a new mail in folder: "+ Type.SPAM.name());
        } else {
            statement.setString(9, Type.INBOX.name());
            System.out.println("have a new mail in folder: "+ Type.INBOX.name());
        }
        statement.setString(10, subject);
    }

    private void handleFile(Messages messages, List<FileData> fileDatas) {
        if (CollectionUtils.isNotEmpty(fileDatas)) {
            fileDatas.stream().forEach(x -> {
                try {
                    PreparedStatement statementFile = connection.prepareStatement("INSERT INTO FileData(file_id, file_name,base64, email_id) VALUES (?,?,?,?)");
                    setFile(x.getFilename(), x.getBase64Content(), messages.getMessageId(), statementFile);
                    statementFile.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void setFile(String fileName, String base64, String emailId, PreparedStatement statementFile) throws SQLException {
        statementFile.setString(1, UUID.randomUUID().toString());
        statementFile.setString(2, fileName);
        statementFile.setString(3, base64);
        statementFile.setString(4, emailId);
    }

    public String getBaseFile(String fileId){
        try (PreparedStatement statement = connection.prepareStatement(
                "Select f.base64 from FileData f where f.file_id = '" + fileId + "'")) {
            while (statement.executeQuery().next()) {
                return statement.getResultSet().getString(1);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
