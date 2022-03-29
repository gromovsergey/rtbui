package app.programmatic.ui.payment.paypal.dto.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptureDto {
    private String id;
    private String status;
    private AmountDto amount;
    @JsonProperty("final_capture")
    private String finalCapture;
    @JsonProperty("seller_protection")
    private SellerProtectionDto sellerProtection;
    @JsonProperty("seller_receivable_breakdown")
    private SellerReceivableBreakdownDto sellerReceivableBreakdown;
    private Set<LinkDto> links;
    @JsonProperty("create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createTime;
    @JsonProperty("update_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updateTime;
}
