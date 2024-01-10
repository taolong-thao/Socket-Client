package async;


import connection.ConfigManagement;
import connection.ConnectorManager;
import model.Messages;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import service.MailService;
import utils.SendUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static async.ExcuteMessages.getNewMessages;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class EmailFetcherTask implements Runnable {
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Logger logger = LogManager.getLogger(EmailFetcherTask.class);
    Properties config = ConfigManagement.getConfig();

    @Override
    public void run() {
        try {
            login();
            List<String> idsByAccount = MailService.getAllByAccount(SendUtils.account);
            if (CollectionUtils.isNotEmpty(idsByAccount)) {
                ExcuteMessages.idsFromMail = idsByAccount;
            }
            boolean checkMailServer = SendUtils.sendAndReturnForAsyncUIDL(writer, "UIDL", reader);
            if (checkMailServer) {
                asyncMail();
            }
        } catch (IOException e) {
            System.out.println("error check async " + e.getMessage());
        }
    }

    private void login() throws IOException {
        Socket server = ConnectorManager.connectToServer(config.getProperty("pop3.host"), Integer.parseInt(config.getProperty("pop3.port")));
        reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        SendUtils.sendForAsync(writer, "USER " + config.getProperty("server.user"), reader);
        SendUtils.sendForAsync(writer, "PASS " + config.getProperty("server.password"), reader);
    }

    private void asyncMail() {
        try {
            login();

//            DOMConfigurator.configure(config.getProperty("server.log"));
//            logger.info(reader.readLine());
            List<Messages> messages = getNewMessages(writer, reader);
            if (CollectionUtils.isEmpty(messages)) {
                return;
            }
            for (int index = 0; index < messages.size(); index++) {
                if (!MailService.checkExistById(messages.get(index).getMessageId())) {
                    MailService.saveEmail(messages.get(index));
                }
            }
            ExcuteMessages.messages = new ArrayList<>();
            SendUtils.sendForAsync(writer, "QUIT", reader);
            reader.readLine();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
