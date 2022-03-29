package app.programmatic.ui.payment.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
    @NotNull
    @JsonProperty("payer_id")
    private Integer payerId;
    @NotNull
    private String currency;
    @NotNull
    private BigDecimal amount;
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("cancel_url")
    private String cancelUrl;
}
