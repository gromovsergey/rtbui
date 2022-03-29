package app.programmatic.ui.kickcaptcha.repository;

import app.programmatic.ui.kickcaptcha.model.PasswordResetTokenEntity;
import app.programmatic.ui.kickcaptcha.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    PasswordResetTokenEntity findByToken(String token);

    PasswordResetTokenEntity findByUser(UserEntity userEntity);

    Stream<PasswordResetTokenEntity> findAllByExpiryTimeIsLessThan(LocalDateTime now);

    void deleteByExpiryTimeIsLessThan(LocalDateTime now);

    @Modifying
    @Query("delete from PasswordResetTokenEntity t where t.expiryTime <= ?1")
    void deleteAllExpiredSince(LocalDateTime now);
}
