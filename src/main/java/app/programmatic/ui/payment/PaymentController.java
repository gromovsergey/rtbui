package app.programmatic.ui.payment;

import app.programmatic.ui.payment.paypal.dto.CreateOrderRequestDto;
import app.programmatic.ui.payment.paypal.dto.OrderResponseDto;
import app.programmatic.ui.payment.paypal.dto.external.OrderDto;
import app.programmatic.ui.payment.paypal.service.PaypalClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;

@RestController
@RequestMapping("/rest/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaypalClient paypalClient;

    @RequestMapping(method = RequestMethod.POST, path = "/create", produces = "application/json")
    public ResponseEntity createOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
        try {
            OrderResponseDto createOrderResponse = paypalClient.createOrder(createOrderRequestDto);
            return ResponseEntity.created(URI.create(createOrderResponse.getPaymentUrl())).body(createOrderResponse);
        } catch (Exception e) {
            log.error("Failed to create paypal order payment", e);
            return ResponseEntity.badRequest().body("Failed to create paypal order payment");
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{orderId}/capture", produces = "application/json")
    public ResponseEntity captureOrder(@PathVariable(value = "orderId") String orderId) {
        try {
            OrderResponseDto createOrderResponse = paypalClient.captureOrder(orderId);
            return ResponseEntity.ok(createOrderResponse);
        } catch (HttpStatusCodeException e) {
            log.error("Failed to capture paypal payment", e);
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(), e.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to capture paypal payment", e);
            return ResponseEntity.badRequest().body("Failed to capture paypal payment");
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{orderId}", produces = "application/json")
    public ResponseEntity getOrderDetails(@PathVariable(value = "orderId") String orderId) {
        try {
            OrderDto orderDetails = paypalClient.getOrderDetails(orderId);
            return ResponseEntity.ok(orderDetails);
        } catch (HttpStatusCodeException e) {
            log.error("Failed to get paypal order payment details", e);
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(), e.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to get paypal order payment details", e);
            return ResponseEntity.badRequest().body("Failed to get paypal order payment details");
        }
    }
}
