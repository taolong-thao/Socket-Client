package model;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class Account {

    private final String email;
    private String password;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }


}
