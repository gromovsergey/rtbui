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
public class PurchaseUnitDto {

    /**
     * The API caller-provided external ID for the purchase unit. Required for multiple purchase units when you must update the order through PATCH.
     * If you omit this value and the order contains only one purchase unit, PayPal sets this value to default.
     * Note: If there are multiple purchase units, reference_id is required for each purchase unit.
     * Maximum length: 256.
     */
    @JsonProperty("reference_id")
    private String referenceId;
    private AmountDto amount;
    private PayeeDto payee;
    private ShippingDto shipping;
    private PaymentsDto payments;
}
