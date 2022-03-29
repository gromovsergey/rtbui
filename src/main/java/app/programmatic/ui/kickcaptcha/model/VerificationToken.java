package app.programmatic.ui.kickcaptcha.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha.verification_token")
public class VerificationToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private UserEntity user;

    @Column(name = "expiry_datetime")
    private LocalDateTime expiryTime;

    public VerificationToken() {
        super();
    }

    public VerificationToken(final String token) {
        super();

        this.token = token;
        this.expiryTime = calculateExpiryDateTime(EXPIRATION);
    }

    public VerificationToken(final String token, final UserEntity user) {
        super();

        this.token = token;
        this.user = user;
        this.expiryTime = calculateExpiryDateTime(EXPIRATION);
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(final UserEntity userEntity) {
        this.user = userEntity;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(final LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    private LocalDateTime calculateExpiryDateTime(final int expiryTimeInMinutes) {
         LocalDateTime time =  LocalDateTime.now();
         time.plusMinutes(expiryTimeInMinutes);
         return time;
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryTime = calculateExpiryDateTime(EXPIRATION);
    }

    //

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getExpiryTime() == null) ? 0 : getExpiryTime().hashCode());
        result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
        result = prime * result + ((getUser() == null) ? 0 : getUser().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VerificationToken other = (VerificationToken) obj;
        if (getExpiryTime() == null) {
            if (other.getExpiryTime() != null) {
                return false;
            }
        } else if (!getExpiryTime().equals(other.getExpiryTime())) {
            return false;
        }
        if (getToken() == null) {
            if (other.getToken() != null) {
                return false;
            }
        } else if (!getToken().equals(other.getToken())) {
            return false;
        }
        if (getUser() == null) {
            if (other.getUser() != null) {
                return false;
            }
        } else if (!getUser().equals(other.getUser())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Token [String=").append(token).append("]").append("[Expires").append(expiryTime).append("]");
        return builder.toString();
    }

}
