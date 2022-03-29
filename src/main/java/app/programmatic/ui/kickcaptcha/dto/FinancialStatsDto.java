package app.programmatic.ui.kickcaptcha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinancialStatsDto {
    @JsonProperty("account_id")
    Integer accountId;
    @JsonProperty("transaction_id")
    String txId;
    LocalDateTime time;
    BigDecimal refill;
}
