package app.programmatic.ui.kickcaptcha.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "captcha.requeststatshourly")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(RecognitionStatsIdEntity.class)
public class RecognitionStatsEntity {

    @Id
    @NotNull
    @Column(nullable = false, name = "account_id")
    Integer accountId;

    @Id
    @NotNull
    @Column(nullable = false, name = "sdate")
    LocalDate date;

    @Id
    @NotNull
    @Column(nullable = false)
    Integer hour;

    @NotNull
    @Column(nullable = false)
    @ColumnDefault("0")
    Integer requests;

    @NotNull
    @Column(nullable = false, name = "solved_requests")
    @ColumnDefault("0")
    Integer solvedRequests;

    @NotNull
    @Column(nullable = false)
    @ColumnDefault("0")
    BigDecimal amount;
}
