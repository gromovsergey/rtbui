package app.programmatic.ui.payment.paypal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {
    @JsonProperty("payment_id")
    private String paymentId;
    private String status;
    private String paymentUrl;
}
