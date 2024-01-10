package async;

import connection.ConfigManagement;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class EmailFetcherScheduler {

    public static void asyncMail() {
        Properties config = ConfigManagement.getConfig();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Integer.parseInt(config.getProperty("threadPoolSize")));
        // Define the task to fetch emails
        Runnable emailFetcherTask = new EmailFetcherTask();
        // Schedule the task to run every time in config file
        scheduler.scheduleAtFixedRate(emailFetcherTask, 0, Long.parseLong(config.getProperty("timeRefresh")), TimeUnit.SECONDS);
    }
}
