package utils;

import model.Messages;
import org.apache.commons.lang3.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class HandleLogicUtils {
    public static String getDateTime() {
        Date dateTimeObject = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        String dateTime = dateFormat.format(dateTimeObject);
        return dateTime;
    }

    public static String getHeaderValue(Messages messages, String header) {
        if (messages.getHeaders().containsKey(header) && messages.getHeaders().get(header) != null) {
            return messages.getHeaders().get(header).toString();
        } else {
            return "";
        }
    }

    public static Set<String> getFilterConfig(String filter) {
        Set<String> arrayFilter = new HashSet<>();
        if (ObjectUtils.isEmpty(filter)) {
            return null;
        } else {
            String[] filters = filter.split(":");
            for (String temp : filters) {
                arrayFilter.add(temp.trim());
            }
            return arrayFilter;
        }
    }
}
