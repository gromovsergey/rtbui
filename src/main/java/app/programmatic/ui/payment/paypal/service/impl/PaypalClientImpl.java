package app.programmatic.ui.payment.paypal.service.impl;

import app.programmatic.ui.payment.dao.PaymentTransactionsDao;
import app.programmatic.ui.payment.dao.model.PaymentTransactionEntity;
import app.programmatic.ui.payment.paypal.converter.OrderDtoMapper;
import app.programmatic.ui.payment.paypal.dto.CreateOrderRequestDto;
import app.programmatic.ui.payment.paypal.dto.OrderResponseDto;
import app.programmatic.ui.payment.paypal.dto.external.OrderDto;
import app.programmatic.ui.payment.paypal.service.PaypalClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaypalClientImpl implements PaypalClient {
    private static final String CHECKOUT_PATH = "/v2/checkout/orders";

    @Qualifier("paypalRestTemplate")
    private final OAuth2RestTemplate restTemplate;
    private final OrderDtoMapper orderDtoMapper;
    private final PaymentTransactionsDao paymentTransactionsDao;

    @Override
    public OrderResponseDto createOrder(CreateOrderRequestDto createOrderRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=representation");
        OrderDto orderDto = orderDtoMapper.createOrderRequestDtoToOrderDto(createOrderRequestDto);
        HttpEntity<Object> request = new HttpEntity<>(orderDto, headers);

        OrderDto response = restTemplate.exchange(CHECKOUT_PATH, HttpMethod.POST, request, OrderDto.class).getBody();

        PaymentTransactionEntity paymentTransactionEntity = orderDtoMapper.orderDtoToPaymentTransactionEntity(response,
                createOrderRequestDto.getPayerId());
        paymentTransactionsDao.createPaymentTransaction(paymentTransactionEntity);

        return orderDtoMapper.orderDtoToCreateOrderResponseDto(response);
    }

    @Override
    public OrderResponseDto captureOrder(String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        OrderDto response = restTemplate.exchange(CHECKOUT_PATH + "/{orderId}/capture", HttpMethod.POST, request, OrderDto.class, orderId)
                .getBody();

        PaymentTransactionEntity transaction = paymentTransactionsDao.getPaymentTransactionById(orderId);
        if (transaction != null) {
            PaymentTransactionEntity transactionWithUpdatedFields = transaction.withStatus(response.getStatus())
                    .withUpdateTime(Date.from(Instant.now()));
            paymentTransactionsDao.updatePaymentTransaction(transactionWithUpdatedFields);
        } else {
            log.error("No records with orderId " + orderId + " in database");
        }

        return orderDtoMapper.orderDtoToCreateOrderResponseDto(response);
    }

    @Override
    public OrderDto getOrderDetails(String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        return restTemplate.exchange(CHECKOUT_PATH + "/{orderId}", HttpMethod.GET, request, OrderDto.class, orderId).getBody();
    }
}
