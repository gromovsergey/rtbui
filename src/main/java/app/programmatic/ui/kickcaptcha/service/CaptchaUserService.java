package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.dto.UserDto;
import app.programmatic.ui.kickcaptcha.model.PasswordResetTokenEntity;
import app.programmatic.ui.kickcaptcha.model.UserEntity;
import app.programmatic.ui.kickcaptcha.model.VerificationToken;

import java.util.List;
import java.util.Optional;

public interface CaptchaUserService {

    UserEntity registerNewUserAccount(UserDto accountDto);

    UserEntity getUser(String verificationToken);

    void saveRegisteredUser(UserEntity userEntity);

    void deleteUser(UserEntity userEntity);

    void createVerificationTokenForUser(UserEntity userEntity, String token);

    VerificationToken getVerificationToken(UserEntity userEntity);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(UserEntity userEntity, String token);

    UserEntity findUserByEmail(String email);

    PasswordResetTokenEntity getPasswordResetToken(String token);

    Optional<UserEntity> getUserByPasswordResetToken(String token);

    Optional<UserEntity> getUserByID(int id);

    void changeUserPassword(UserEntity userEntity, String password);

    boolean checkIfValidOldPassword(UserEntity userEntity, String password);

    String validateVerificationToken(String token);

    List<String> getUsersFromSessionRegistry();
}
