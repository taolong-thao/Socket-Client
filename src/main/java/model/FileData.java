package model;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class FileData {
    String file_id;
    private String filename;
    private String base64Content;

    String email_id;

    public FileData(String filename, String base64Content) {
        this.filename = filename;
        this.base64Content = base64Content;
    }

    public FileData(String file_id, String filename, String base64Content, String email_id) {
        this.file_id = file_id;
        this.filename = filename;
        this.base64Content = base64Content;
        this.email_id = email_id;
    }

    public String getFilename() {
        return filename;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}

