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
public class OrderDto {

    /**
     * The intent to either capture payment immediately or authorize a payment for an order after order creation.
     * <p>
     * The possible values are:
     * <p>
     * CAPTURE - The merchant intends to capture payment immediately after the customer makes a payment.
     * <p>
     * AUTHORIZE - The merchant intends to authorize a payment and place funds on hold after the customer makes a payment.
     * Authorized payments are guaranteed for up to three days but are available to capture for up to 29 days.
     * After the three-day honor period, the original authorized payment expires and you must re-authorize the payment.
     * You must make a separate request to capture payments on demand. This intent is not supported when you have more than one `purchase_unit`
     * within your order.
     */
    private String intent;
    private String id;
    /**
     * The order status.
     * <p>
     * The possible values are:
     * <p>
     * CREATED. The order was created with the specified context.
     * SAVED. The order was saved and persisted.
     * The order status continues to be in progress until a capture is made with final_capture = true for all purchase units within the order.
     * APPROVED. The customer approved the payment through the PayPal wallet or another form of guest or unbranded payment.
     * For example, a card, bank account, or so on.
     * VOIDED. All purchase units in the order are voided.
     * COMPLETED. The payment was authorized or the authorized payment was captured for the order.
     * PAYER_ACTION_REQUIRED. The order requires an action from the payer (e.g. 3DS authentication).
     * Redirect the payer to the "rel":"payer-action" HATEOAS link returned as part of the response prior to authorizing or capturing the order.
     */
    private String status;
    @JsonProperty("purchase_units")
    private Set<PurchaseUnitDto> purchaseUnits;
    @JsonProperty("application_context")
    private ApplicationContextDto applicationContext;
    private PayerDto payer;
    @JsonProperty("create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createTime;
    private Set<LinkDto> links;
}
