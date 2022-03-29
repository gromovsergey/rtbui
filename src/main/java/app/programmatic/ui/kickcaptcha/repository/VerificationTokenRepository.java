package app.programmatic.ui.kickcaptcha.repository;

import app.programmatic.ui.kickcaptcha.model.UserEntity;
import app.programmatic.ui.kickcaptcha.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(UserEntity userEntity);

    Stream<VerificationToken> findAllByExpiryTimeIsLessThan(LocalDateTime now);

    void deleteByExpiryTimeIsLessThan(LocalDateTime now);

    @Modifying
    @Query("delete from VerificationToken t where t.expiryTime <= ?1")
    void deleteAllExpiredSince(LocalDateTime now);
}
