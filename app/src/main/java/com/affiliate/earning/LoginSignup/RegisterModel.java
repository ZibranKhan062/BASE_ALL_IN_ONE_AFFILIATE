package com.affiliate.earning.LoginSignup;

public class RegisterModel {

    String name;
    String email;
    String referralCode;


    public RegisterModel() {
    }

    public RegisterModel(String name, String email, String referralCode) {
        this.name = name;
        this.email = email;
        this.referralCode = referralCode;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getReferralCode() {
        return referralCode;
    }
}
