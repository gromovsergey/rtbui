package app.programmatic.ui.kickcaptcha.dto;

import app.programmatic.ui.kickcaptcha.model.UserStatusEnum;
import app.programmatic.ui.kickcaptcha.validation.PasswordMatches;
import app.programmatic.ui.kickcaptcha.validation.ValidEmail;
import app.programmatic.ui.kickcaptcha.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@PasswordMatches
@Data
public class UserDto {

    @NotNull
    @Size(min=1)
    private String login;

    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @NotNull
    @ValidEmail
    private String email;

    private String uid;
}
