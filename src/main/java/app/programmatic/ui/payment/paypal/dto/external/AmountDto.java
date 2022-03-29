package app.programmatic.ui.payment.paypal.dto.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmountDto {
    @JsonProperty("currency_code")
    private String currencyCode;
    private String value;
}
