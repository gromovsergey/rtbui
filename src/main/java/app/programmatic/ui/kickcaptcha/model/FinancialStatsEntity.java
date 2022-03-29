package app.programmatic.ui.kickcaptcha.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha.financialtransactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(FinancialStatsIdEntity.class)
public class FinancialStatsEntity {

    @Id
    @NotNull
    @Column(nullable = false, updatable = false, name = "account_id")
    Integer accountId;

    @Id
    @NotNull
    @Column(nullable = false, updatable = false, name = "transaction_id")
    String txId;

    @NotNull
    LocalDateTime time;

    @NotNull
    BigDecimal refill;
}
