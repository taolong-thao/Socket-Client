package model;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class Email {
    String mailId;
    int isNew;
    String bodyMail;
    String fromMail;
    String toMail;
    String ccMail;
    String bccMail;
    String type;
    String accountId;

    String subject;

    List<FileData> fileDataList = new ArrayList<>();

    public Email(String mailId, int isNew, String bodyMail, String fromMail, String toMail, String ccMail, String bccMail, String type, String accountId, String subject) {
        this.mailId = mailId;
        this.isNew = isNew;
        this.bodyMail = bodyMail;
        this.fromMail = fromMail;
        this.toMail = toMail;
        this.ccMail = ccMail;
        this.bccMail = bccMail;
        this.type = type;
        this.accountId = accountId;
        this.subject = subject;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public int isNew() {
        return isNew;
    }

    public void setNew(int aNew) {
        isNew = aNew;
    }

    public String getBodyMail() {
        return bodyMail;
    }

    public void setBodyMail(String bodyMail) {
        this.bodyMail = bodyMail;
    }

    public String getFromMail() {
        return fromMail;
    }

    public void setFromMail(String fromMail) {
        this.fromMail = fromMail;
    }

    public String getToMail() {
        return toMail;
    }

    public void setToMail(String toMail) {
        this.toMail = toMail;
    }

    public String getCcMail() {
        return ccMail;
    }

    public void setCcMail(String ccMail) {
        this.ccMail = ccMail;
    }

    public String getBccMail() {
        return bccMail;
    }

    public void setBccMail(String bccMail) {
        this.bccMail = bccMail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<FileData> getFileDataList() {
        return fileDataList;
    }

    public void setFileDataList(List<FileData> fileDataList) {
        this.fileDataList = fileDataList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
