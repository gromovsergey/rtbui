package app.programmatic.ui.payment.paypal.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SellerReceivableBreakdownDto {
    @JsonProperty("gross_amount")
    private AmountDto grossAmount;
    @JsonProperty("paypal_fee")
    private AmountDto paypalFee;
    @JsonProperty("net_amount")
    private AmountDto netAmount;
}
