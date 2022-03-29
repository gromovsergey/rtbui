package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.dto.UserDto;
import app.programmatic.ui.kickcaptcha.error.UserAlreadyExistException;
import app.programmatic.ui.kickcaptcha.model.PasswordResetTokenEntity;
import app.programmatic.ui.kickcaptcha.model.UserEntity;
import app.programmatic.ui.kickcaptcha.model.UserStatusEnum;
import app.programmatic.ui.kickcaptcha.model.VerificationToken;
import app.programmatic.ui.kickcaptcha.repository.CaptchaUserRepository;
import app.programmatic.ui.kickcaptcha.repository.PasswordResetTokenRepository;
import app.programmatic.ui.kickcaptcha.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CaptchaUserServiceImpl implements CaptchaUserService {

    @Autowired
    private CaptchaUserRepository captchaUserRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private RoleRepository roleRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private Environment env;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "SpringRegistration";

    // API

    @Override
    public UserEntity registerNewUserAccount(final UserDto accountDto) {
        if (emailExists(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
        }
        final UserEntity userEntity = new UserEntity();

        userEntity.setLogin(accountDto.getLogin());
        userEntity.setEmail(accountDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        userEntity.setApiKey(userEntity.getPassword());
        userEntity.setUid(accountDto.getUid());
//        user.setStatus(StatusEnum.valueOf(accountDto.getStatus()));
        userEntity.setStatus(UserStatusEnum.EMAIL_SENT);

//        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return captchaUserRepository.save(userEntity);
    }

    @Override
    public UserEntity getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(UserEntity userEntity) {
        return tokenRepository.findByUser(userEntity);
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(final UserEntity userEntity) {
        captchaUserRepository.save(userEntity);
    }

    @Override
    public void deleteUser(final UserEntity userEntity) {
        final VerificationToken verificationToken = tokenRepository.findByUser(userEntity);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetTokenEntity passwordToken = passwordTokenRepository.findByUser(userEntity);

        if (passwordToken != null) {
            passwordTokenRepository.delete(passwordToken);
        }

        captchaUserRepository.delete(userEntity);
    }

    @Override
    public void createVerificationTokenForUser(final UserEntity userEntity, final String token) {
        final VerificationToken myToken = new VerificationToken(token, userEntity);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID()
            .toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(final UserEntity userEntity, final String token) {
        final PasswordResetTokenEntity myToken = new PasswordResetTokenEntity(token, userEntity);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public UserEntity findUserByEmail(final String email) {
        return captchaUserRepository.findByEmail(email);
    }

    @Override
    public PasswordResetTokenEntity getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token) .getUser());
    }

    @Override
    public Optional<UserEntity> getUserByID(final int id) {
        return captchaUserRepository.findByAccountId(id);
    }

    @Override
    public void changeUserPassword(final UserEntity userEntity, final String password) {
        userEntity.setPassword(passwordEncoder.encode(password));
        captchaUserRepository.save(userEntity);
    }

    @Override
    public boolean checkIfValidOldPassword(final UserEntity userEntity, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, userEntity.getPassword());
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final UserEntity userEntity = verificationToken.getUser();
        if (LocalDateTime.now()
                .isAfter(verificationToken.getExpiryTime())) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }
//
//        user.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        captchaUserRepository.save(userEntity);
        return TOKEN_VALID;
    }

    private boolean emailExists(final String email) {
        return captchaUserRepository.findByEmail(email) != null;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals()
            .stream()
            .filter((u) -> !sessionRegistry.getAllSessions(u, false)
                .isEmpty())
            .map(o -> {
                if (o instanceof UserEntity) {
                    return ((UserEntity) o).getEmail();
                } else {
                    return o.toString()
            ;
                }
            }).collect(Collectors.toList());
    }

}
