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
public class PayerDto {
    @JsonProperty("email_address")
    private String emailAddress;
    @JsonProperty("payer_id")
    private String payerId;
    private NameDto name;
    private AddressDto address;
}
