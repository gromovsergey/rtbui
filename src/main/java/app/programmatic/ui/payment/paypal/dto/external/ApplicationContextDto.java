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
public class ApplicationContextDto {
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("cancel_url")
    private String cancelUrl;
}
