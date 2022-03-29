package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.model.PasswordResetTokenEntity;
import app.programmatic.ui.kickcaptcha.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserSecurityServiceImpl implements SecurityUserService {

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetTokenEntity passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetTokenEntity passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetTokenEntity passToken) {
        return passToken.getExpiryTime().isBefore(LocalDateTime.now());
    }
}
