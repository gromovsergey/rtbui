package app.programmatic.ui.kickcaptcha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecognitionStatsDto {
    @JsonProperty("account_id")
    Integer accountId;
    @JsonProperty("sdate")
    LocalDate date;
    Integer hour;
    Integer requests;
    @JsonProperty("solved_requests")
    Integer solvedRequests;
    BigDecimal amount;
}
