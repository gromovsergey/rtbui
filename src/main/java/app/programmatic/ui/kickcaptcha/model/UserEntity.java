package app.programmatic.ui.kickcaptcha.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "captcha.account")
@Getter
@Setter
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class UserEntity {

    @SequenceGenerator(name = "AccountIdGen", sequenceName = "captcha.account_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountIdGen")
    @Column(name = "account_id", unique=true, nullable = false, updatable = false)
    private Integer accountId;

    private String login;

    @Column(unique = true, nullable = false, name = "api_key")
    private String apiKey;

    @Column(length = 60)
    private String password;

    private String email;

    private String uid;

//    @Enumerated(EnumType.STRING)
//    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @ColumnDefault("EMAIL_SENT")
    private UserStatusEnum status;

    @Column(nullable = false, name = "total_refill")
    @ColumnDefault("0")
    @Generated(GenerationTime.INSERT)
    private BigDecimal totalRefill;

    @Column(nullable = false, name = "total_amount")
    @ColumnDefault("0")
    @Generated(GenerationTime.INSERT)
    private BigDecimal totalAmount;


    //
/*
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
        super();
        this.status = StatusEnum.EMAIL_SENT;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }

    public void setStatus(final  StatusEnum status){
        this.status = status;
    }

    public StatusEnum getStatus(){
        return this.status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
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
        final User user = (User) obj;
        if (!getEmail().equals(user.getEmail())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=")
                .append(id)
                .append(", firstName=").append(firstName)
                .append(", lastName=").append(lastName)
                .append(", email=").append(email)
                .append(", status=").append(status)
                .append(", roles=").append(roles)
                .append("]");
        return builder.toString();
    }


 */
}
