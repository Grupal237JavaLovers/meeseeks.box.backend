package meeseeks.box.domain.dto;

import javax.validation.constraints.NotNull;

/**
 * @author Tiron Andreea-Ecaterina
 * @version 1.0
 */

public class ConsumerDto {
    private String username;

    @NotNull(message = "{provider.email.null}")
    private String email;
    private String password;
    private String confirmPassword;
    private String realName;
    private String profileImage;

    public ConsumerDto() {
    }

    public ConsumerDto(String username, String email, String password, String confirmPassword, String realName, String profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.realName = realName;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
