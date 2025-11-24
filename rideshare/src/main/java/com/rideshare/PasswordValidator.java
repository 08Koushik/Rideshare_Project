package com.rideshare;

public class PasswordValidator {
    public boolean validatePassword(String password) throws InfyAcademyException{
        if(password==null||password.isBlank()){
            throw new InfyAcademyException("invalid password");
        }
        return password.matches("[A-Za-z0-9]{8,20}");
    }
}
