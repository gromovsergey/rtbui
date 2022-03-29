package app.programmatic.ui.payment.dao.model;

import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.user.dao.model.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@With
@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionEntity extends VersionEntityBase<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private Long id;

    @NotNull
    @Column(length = 20, nullable = false, updatable = false, name = "payment_id")
    private String paymentId;

    @NotNull
    @Column(length = 50, nullable = false)
    private String status;

    @NotNull
    @Column(nullable = false)
    private Long amount;

    @NotNull
    @Column(length = 3, nullable = false)
    private String currency;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false, updatable = false)
    private User user;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, updatable = false)
    private Date createTime;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(length = 10, name = "payment_system")
    private String paymentSystem;
}
