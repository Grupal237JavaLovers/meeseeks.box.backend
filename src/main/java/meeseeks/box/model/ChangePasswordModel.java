package meeseeks.box.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

public class ChangePasswordModel {

    private String currentPassword;
    @Size(min = 8, message = "{provider.password.length}")
    private String password;
    private String confirmPassword;

    public ChangePasswordModel() {
        this(null, null, null);
    }

    public ChangePasswordModel(final String currentPassword,
                               final String password,
                               final String confirmPassword) {
        this.currentPassword = currentPassword;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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

    @AssertTrue(message = "{provider.passwords.mismatch}")
    public boolean isPasswordConfirmed() {
        return getPassword() != null && this.confirmPassword != null
                && getPassword().equals(this.confirmPassword);
    }
}
