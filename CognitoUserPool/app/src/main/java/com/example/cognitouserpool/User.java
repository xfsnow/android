package com.example.cognitouserpool;

public class User {
    private String userId = "";
    private String password = "";
    private String userGivenName = "";
    // 电话号码必须遵循以下格式规则：+8613522071098 电话号码必须以加号 (+) 开头，后面紧跟国家/地区代码。电话号码只能包含 + 号和数字。您必须先删除电话号码中的任何其他字符，如圆括号、空格或短划线 (-)，然后才能将该值提交给服务。例如，美国境内的电话号码必须遵循以下格式：+14325551212。
    private String phoneNumber = "";
    private String email = "";

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User(String userId, String password, String userGivenName, String phoneNumber, String email) {
        this.userId = userId;
        this.password = password;
        this.userGivenName = userGivenName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserGivenName() {
        return userGivenName;
    }

    public void setUserGivenName(String userGivenName) {
        this.userGivenName = userGivenName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
