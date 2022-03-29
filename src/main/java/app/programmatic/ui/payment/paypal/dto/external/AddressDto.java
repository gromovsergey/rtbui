package app.programmatic.ui.payment.paypal.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto {
    @JsonProperty("address_line_1")
    private String addressLine;
    @JsonProperty("admin_area_1")
    private String adminArea1;
    @JsonProperty("admin_area_2")
    private String adminArea2;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("country_code")
    private String countryCode;
}
