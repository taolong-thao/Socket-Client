import async.EmailFetcherScheduler;
import enums.Type;
import model.Account;
import model.Email;
import model.FileData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import service.MailService;
import service.SendMailService;
import utils.SendUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Base64;


/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class SocketMail {

    private static Scanner scanner;
    private static String email;
    private static String password;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int option;
        scanner = new Scanner(System.in);

        System.out.println("***********************************");
        System.out.println("*                                 *");
        System.out.println("*      Welcome to SocketMail      *");
        System.out.println("*                                 *");
        System.out.println("***********************************");

        while (true) {
            System.out.println("");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.println("");
            System.out.println("Type the number of your option");

            option = readOption(2);
            if (option == 1) {
                performLogin();
            } else if (option == 2) { // ============ EXIT ============ //
                closeConnection();
            } else {
                continue;
            }
            EmailFetcherScheduler.asyncMail();
            boolean loop = true; // it will become false on logout
            while (loop) {
                System.out.println("");
                System.out.println("1. New Email");
                System.out.println("2. Show Emails");
                System.out.println("3. Move Email To Folder");
                System.out.println("4. Logout");
                System.out.println("5. Exit");
                System.out.println("");
                System.out.println("Type the number of your option");

                option = readOption(5);

                if (option == 5) { // ============ EXIT ============ //
                    closeConnection();
                }

                switch (option) {
                    case 1: // ============ NEW EMAIL ============ //
                        sendEmail();
                        break;
                    case 2: // ============ SHOW EMAILS ============ //
                        showEmails();
                        break;
                    case 3: // ============ MOVE Email ============ //
                        moveMailToFolder();
                        break;
                    case 4: // ============ LOGOUT ============ //
                        loop = false;
                        System.out.println("Bye: " + SendUtils.account);
                        SendUtils.account = "";
                        break;
                }

            }
        }

    }

    // Read option while checking it is a valid option
    private static int readOption(int numOfOptions) {
        int option;
        while (true) {
            try {
                option = scanner.nextInt();
                if (option < 1 || option > numOfOptions) {
                    System.out.println("Your option must be an integer between 1 and " + numOfOptions);
                    System.out.println("Type the number of your option");
                    scanner.nextLine(); // to clear the scenner
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Your option must be an integer");
                System.out.println("Type the number of your option");
                scanner.nextLine(); // to clear the scenner
            }
        }
        scanner.nextLine(); // to clear the scenner
        return option;
    }

    private static void performLogin() throws IOException {
        System.out.println("");
        System.out.println("***********************************");
        System.out.println("*****       Login Form        *****");
        System.out.println("***********************************");
        System.out.println("");

        while (true) {

            System.out.println("Type your email");
            email = scanner.nextLine();
            System.out.println("Type your password");
            password = scanner.nextLine();

            Account account = MailService.handleLogin(email, password);
            if (account != null) {
                SendUtils.account = account.getEmail();
                System.out.println("Welcome back " + SendUtils.account);
                break;
            } else {
                System.out.println("Email or password is incorrect. Try again.");
            }
        }

    }

    private static void sendEmail() {

        System.out.println("");
        System.out.println("***********************************");
        System.out.println("*****       New Email         *****");
        System.out.println("***********************************");
        System.out.println("");

        Set<String> tos = new HashSet<>();
        Set<String> ccs = new HashSet<>();
        Set<String> bccs = new HashSet<>();
        Set<File> files = new HashSet<>();

        while (true) {
            System.out.println("Type the To email:");
            String receiver = scanner.nextLine();
            tos.add(receiver);
            System.out.println("Continue? '.' for exit:");
            String qa = scanner.nextLine();
            if (".".equals(qa))
                break;
        }
        while (true) {
            System.out.println("Type the Cc email:(if you want's CC type '.')");
            String cc = scanner.nextLine();
            if (".".equals(cc)) {
                break;
            }
            ccs.add(cc);
        }
        while (true) {
            System.out.println("Type the bcc email:(if you want's Bcc type '.')");
            String bcc = scanner.nextLine();
            if (".".equals(bcc)) {
                break;
            }
            bccs.add(bcc);
        }
        if (CollectionUtils.isEmpty(tos)) {
            System.out.println("To is not empty:");
        }
        System.out.println("Type the subject of the email");
        String subject = scanner.nextLine();
        System.out.println("Type the main body of the email");
        String mainBody = scanner.nextLine();

        while (true) {
            System.out.println("Type the file email: Y/N");
            String file = scanner.nextLine();
            if ("N".equals(file)) {
                break;
            }

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("File Chooser");
                JButton button = new JButton("Choose File");

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        int returnValue = fileChooser.showOpenDialog(null);

                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            if (selectedFile.length() >= 3 * 1024 * 1024) {
                                JOptionPane.showMessageDialog(null, "File size exceeds the limit (3 MB). Please choose another file.");
                            } else {
                                files.add(selectedFile);
                                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                                frame.dispose();
                            }
                        }
                    }
                });

                frame.getContentPane().add(button);
                frame.setSize(300, 200);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            });

            if (!"N".equals(file)) {
                continue;
            }

            System.out.println("File selection finished.");
            break;
        }
        boolean emailSent = SendMailService.sendEmail(tos, ccs, bccs, files, subject, mainBody, SendUtils.account);
        if (emailSent) {
            System.out.println("Email was sent Successfully");
        } else {
            System.out.println("The receiver does not exist. Try again.");
        }

    }

    private static void showEmails() {

        System.out.println("");
        System.out.println("***********************************");
        System.out.println("*****       All folder mail for you ^^!       *****");
        System.out.println("***********************************");
        System.out.println("");
        boolean loop = true;
        while (loop) {
            System.out.println("1. INBOX");
            System.out.println("2. SPAM");
            System.out.println("3. IMPORTANT");
            System.out.println("4. WORK");
            System.out.println("5. PROJECT");
            System.out.println("6. OUT!");
            int choose = scanner.nextInt();
            scanner.nextLine();
            switch (choose) {
                case 1:
                    readMail(Type.INBOX.name());
                    break;
                case 2:
                    readMail(Type.SPAM.name());
                    break;
                case 3:
                    readMail(Type.IMPORTANT.name());
                    break;
                case 4:
                    readMail(Type.WORK.name());
                    break;
                case 5:
                    readMail(Type.PROJECT.name());
                    break;
                case 6:
                    loop = false;
                    break;
            }
        }
    }

    private static void readMail(String type) {
        List<Email> emails = MailService.showAllMail(SendUtils.account, type);
        if (CollectionUtils.isEmpty(emails)) {
            System.out.println("Mail box is empty!");
            return;
        }
        showContent(emails);
        while (true) {
            System.out.println("Which mail ID do you want to read or put 0 to out or put -1 for review mail current box!");
            String choose = scanner.nextLine();
            if (Objects.equals(choose, "0")) {
                break;
            } else if (Objects.equals(choose, "-1")) {
                emails = MailService.showAllMail(SendUtils.account, type);
                showContent(emails);
            } else {
                readEmail(String.valueOf(choose));
            }
        }
    }

    private static void showContent(List<Email> emails) {
        emails.stream().forEach(x -> {
            System.out.println("mail Id: " + x.getMailId() + "\n" +
                    "Subject: " + x.getSubject() + "\n" +
                    "From: " + x.getFromMail() + "\n" +
                    "Status: " + (x.isNew() == 1 ? "unseen" : "seen") + "\n");
        });
    }

    private static void checkFileData(Email email, List<FileData> fileDataList) {
        if (CollectionUtils.isNotEmpty(fileDataList)) {
            Map<String, String> fileMap = new HashMap<>();
            fileDataList.stream().map(x -> fileMap.put(x.getFile_id(), x.getFilename())).collect(Collectors.toList());
            System.out.printf("file: " + fileDataList.stream().map(FileData::getFilename).collect(Collectors.toList()) + "\n");
            for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                System.out.println("file ID: " + entry.getKey() + " - file name: " + entry.getValue());
            }
            System.out.println("Do you want to show file? (Y/N)");
            String show = scanner.nextLine();
            if ("Y".equals(show)) {
                while (true) {
                    System.out.println("Input file ID or -1 for out: ");
                    String fileId = scanner.nextLine();
                    if (fileId.equals("-1")) {
                        break;
                    } else if (Desktop.isDesktopSupported()) {
                        try {
                            String base64 = MailService.getBase64(fileId);
                            if(base64!=null){
                                byte[] decodedBytes = Base64.getDecoder().decode(base64.replaceAll("[^A-Za-z0-9+/=]", ""));
                                String file = fileMap.get(fileId);
                                String[]temp = file.split("\\.");

                                // Save the decoded bytes to a temporary file
                                File tempFile = File.createTempFile(temp[0],"." + temp[1]);
                                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                                    outputStream.write(decodedBytes);
                                }

                                // Open the temporary file with the default application
                                Desktop.getDesktop().open(tempFile);
                            }
                        } catch (IOException ex) {
                            System.out.println("Error opening file or OS unsupported: " + ex.getMessage());
                        }
                    }
                }
            }

            System.out.println("Do you want to save file? (Y/N)");
            String rp = scanner.nextLine();
            if ("Y".equals(rp)) {
                fileDataList.forEach(MailService::downloadFile);
            }
        }
    }

    private static void readEmail(String emailId) {

        System.out.println("");
        System.out.println("***********************************");
        System.out.println("*****       Read Email        *****");
        System.out.println("***********************************");
        System.out.println("");

        System.out.println("Type the id of the email you want to read");


        Email email = MailService.readMailById(emailId);
        if (ObjectUtils.isEmpty(email)) {
            System.out.println("Mail not exists");
            return;
        }
        List<FileData> fileDataList = email.getFileDataList();
        System.out.println("mail Id: " + email.getMailId() + "\n" +
                "Subject: " + email.getSubject() + "\n" +
                "From: " + email.getFromMail() + "\n" +
                "To: " + email.getToMail() + "\n" +
                (ObjectUtils.isNotEmpty(email.getCcMail()) ? "Cc: " + email.getCcMail() + "\n" : null) +
                "Body: \"" + email.getBodyMail() + "\"\n");
        checkFileData(email, fileDataList);
        System.out.println("Do you want make unseen this mail? (Y/N)");
        String makeSeen = scanner.nextLine();
        if ("Y".equals(makeSeen)) {
            MailService.makeStateEmail(emailId, 1);
        }
    }

    private static void moveMailToFolder() {

        System.out.println("");
        System.out.println("***********************************");
        System.out.println("*****       Move Email        *****");
        System.out.println("***********************************");
        System.out.println("");

        System.out.println("Type the id of the email you want to move for folder");
        String emailId = scanner.nextLine();
        System.out.println("Type the number folder do you want to move");
        System.out.println("1. INBOX");
        System.out.println("2. SPAM");
        System.out.println("3. IMPORTANT");
        System.out.println("4. WORK");
        System.out.println("5. PROJECT");
        System.out.println("6. Get Back!");
        int folder = scanner.nextInt();
        switch (folder) {
            case 1:
                MailService.moveFolderMail(Type.INBOX, emailId);
                break;
            case 2:
                MailService.moveFolderMail(Type.SPAM, emailId);
                break;
            case 3:
                MailService.moveFolderMail(Type.IMPORTANT, emailId);
                break;
            case 4:
                MailService.moveFolderMail(Type.WORK, emailId);
                break;
            case 5:
                MailService.moveFolderMail(Type.PROJECT, emailId);
                break;
            case 6:
                break;
        }
    }

    public static void closeConnection() {
        System.out.println("Goodbye ^^!");
        System.exit(0);
    }
}
