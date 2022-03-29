package app.programmatic.ui.kickcaptcha.service;

public interface SecurityUserService {

    String validatePasswordResetToken(String token);

}
