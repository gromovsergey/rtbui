package app.programmatic.ui.kickcaptcha.repository;

import app.programmatic.ui.kickcaptcha.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaptchaUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    Optional<UserEntity> findByAccountId(Integer id);

    @Override
    void delete(UserEntity userEntity);

}
