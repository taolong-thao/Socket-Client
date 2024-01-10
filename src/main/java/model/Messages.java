package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */

public class Messages implements Serializable {

    private boolean isNew;

    private String messageId;
    private Map<String, List<String>> headers;

    private String body;

    private List<FileData> fileData;

    public Messages(Map<String, List<String>> headers, String body, List<FileData> fileData, boolean isNew, String messageId) {
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
        this.fileData = fileData;
        this.isNew = isNew;
        this.messageId = messageId;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }


    public boolean isNew(){
        return isNew;
    }

    
    public void read(){
        this.isNew = false;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<model.FileData> getFileData() {
        return fileData;
    }

    public void setFileData(List<FileData> fileData) {
        this.fileData = fileData;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
