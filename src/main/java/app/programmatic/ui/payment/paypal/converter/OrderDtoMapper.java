package app.programmatic.ui.payment.paypal.converter;

import app.programmatic.ui.payment.dao.model.PaymentTransactionEntity;
import app.programmatic.ui.payment.paypal.dto.CreateOrderRequestDto;
import app.programmatic.ui.payment.paypal.dto.OrderResponseDto;
import app.programmatic.ui.payment.paypal.dto.external.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderDtoMapper {

    @Mapping(target = "intent", constant = "CAPTURE")
    @Mapping(target = "applicationContext", source = "createOrderRequestDto", qualifiedByName = "convertApplicationContext")
    @Mapping(target = "purchaseUnits", source = "createOrderRequestDto", qualifiedByName = "convertCreateOrderRequestDtoToPurchaseUnit")
    OrderDto createOrderRequestDtoToOrderDto(CreateOrderRequestDto createOrderRequestDto);

    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "paymentUrl", source = "orderDto", qualifiedByName = "convertPaymentUrl")
    OrderResponseDto orderDtoToCreateOrderResponseDto(OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentId", source = "orderDto.id")
    @Mapping(target = "amount", source = "orderDto", qualifiedByName = "convertPurchaseUnitToAmountWithPenny")
    @Mapping(target = "currency", source = "orderDto", qualifiedByName = "convertPurchaseUnitToCurrency")
    @Mapping(target = "user.id", source = "payerId")
    @Mapping(target = "updateTime", source = "orderDto.createTime")
    @Mapping(target = "paymentSystem", constant = "paypal")
    PaymentTransactionEntity orderDtoToPaymentTransactionEntity(OrderDto orderDto, Integer payerId);

    @Named("convertApplicationContext")
    default ApplicationContextDto convertApplicationContext(CreateOrderRequestDto value) {
        return ApplicationContextDto.builder()
                .returnUrl(value.getReturnUrl())
                .cancelUrl(value.getCancelUrl())
                .build();
    }

    @Named("convertCreateOrderRequestDtoToPurchaseUnit")
    default Set<PurchaseUnitDto> convertCreateOrderRequestDtoToPurchaseUnit(CreateOrderRequestDto value) {
        return Collections.singleton(PurchaseUnitDto.builder()
                .amount(AmountDto.builder()
                        .currencyCode(value.getCurrency())
                        .value(value.getAmount().toPlainString())
                        .build())
                .build());
    }

    @Named("convertPurchaseUnitToAmountWithPenny")
    default Long convertPurchaseUnitToAmountWithPenny(OrderDto value) {
        BigDecimal amount = new BigDecimal(value.getPurchaseUnits().iterator().next().getAmount().getValue());
        return amount.multiply(new BigDecimal(100)).longValue();
    }

    @Named("convertPurchaseUnitToCurrency")
    default String convertPurchaseUnitToCurrency(OrderDto value) {
        return value.getPurchaseUnits().iterator().next().getAmount().getCurrencyCode();
    }

    @Named("convertPaymentUrl")
    default String convertPaymentUrl(OrderDto value) {
        Map<String, String> collect = value.getLinks().stream().collect(Collectors.toMap(LinkDto::getRel, LinkDto::getHref));
        return collect.get("approve");
    }
}
