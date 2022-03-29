package app.programmatic.ui.payment.paypal.service;

import app.programmatic.ui.payment.paypal.dto.CreateOrderRequestDto;
import app.programmatic.ui.payment.paypal.dto.OrderResponseDto;
import app.programmatic.ui.payment.paypal.dto.external.OrderDto;

public interface PaypalClient {

    /**
     * Creates an order. Supports orders with only one purchase unit.
     *
     * @param createOrderRequestDto
     * @return CreateOrderResponseDto
     */
    OrderResponseDto createOrder(CreateOrderRequestDto createOrderRequestDto);

    /**
     * Captures a payment for an order.
     *
     * @param orderId
     * @return OrderDto
     */
    OrderResponseDto captureOrder(String orderId);

    /**
     * Shows details for an order, by ID.
     *
     * @param orderId
     * @return OrderDto
     */
    OrderDto getOrderDetails(String orderId);
}
