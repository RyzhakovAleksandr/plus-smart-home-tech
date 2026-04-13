package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    private static final double TAX_RATE = 0.1;

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        log.info(Message.CREATED_PAYMENT, order.getOrderId());

        if (order.getProductPrice() == null) {
            log.error(Message.ERROR_NOT_PRICE, order.getOrderId());
            throw new IllegalArgumentException(Message.NOT_PRICE_EXCEPTION);
        }

        if (order.getDeliveryPrice() == null) {
            log.error(Message.ERROR_NOT_SUM_DELIVERY, order.getOrderId());
            throw new IllegalArgumentException(Message.NOT_SUM_DELIVERY_EXCEPTION);
        }

        Double productCost = order.getProductPrice();
        Double deliveryCost = order.getDeliveryPrice();
        Double feeTotal = productCost * TAX_RATE;
        Double totalCost = productCost + feeTotal + deliveryCost;

        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .productCost(productCost)
                .deliveryCost(deliveryCost)
                .totalCost(totalCost)
                .feeTotal(feeTotal)
                .state(PaymentState.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info(Message.PAYMENT_CREATED, savedPayment.getPaymentId());

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public Double productCost(OrderDto order) {
        log.info(Message.SUM_COUNT_ORDER, order.getOrderId());

        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            log.warn(Message.ORDER_NOT_HAVE_PRODUCT, order.getOrderId());
            throw new NotEnoughInfoInOrderToCalculateException(Message.ORDER_NOT_HAVE_PRODUCT_EXCEPTION);
        }

        double totalProductCost = 0.0;

        List<UUID> productsIds = new ArrayList<>(order.getProducts().keySet());
        List<ProductDto> products = shoppingStoreClient.getProducts(productsIds);

        Map<UUID, ProductDto> productsMap = products.stream()
                .collect(Collectors.toMap(
                        ProductDto::getProductId,
                        Function.identity()
                ));

        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductDto product = productsMap.get(productId);

            if (product == null || product.getPrice() == null) {
                log.error(Message.PRODUCT_NOT_FOUND_HAVE_PRICE, productId);
                throw new NotEnoughInfoInOrderToCalculateException(Message.PRODUCT_NOT_FOUND_HAVE_PRICE_EXCEPTION);
            }

            totalProductCost += product.getPrice() * quantity;
        }
        log.info(Message.TOTAL_SUM_ORDER, order.getOrderId(), totalProductCost);
        return totalProductCost;
    }

    @Override
    public Double getTotalCost(OrderDto order) {
        log.info(Message.GETTING_TOTAL_SUM_ORDER, order.getOrderId());

        Double productCost = order.getProductPrice();
        if (productCost == null) {
            productCost = productCost(order);
        }

        Double feeTotal = productCost * TAX_RATE;
        Double deliveryCost = order.getDeliveryPrice();

        if (deliveryCost == null) {
            deliveryCost = 0.0;
        }

        Double totalCost = productCost + feeTotal + deliveryCost;

        log.info(Message.INFO_TOTAL_SUM,
                order.getOrderId(), productCost, deliveryCost, feeTotal, totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info(Message.WORK_PAYMENT_ORDER, paymentId);

        Payment payment = findPendingPaymentOrThrow(paymentId);

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.paymentSuccess(payment.getOrderId());
        log.info(Message.MESSAGE_PAYMENT_OK, payment.getOrderId());
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info(Message.PAYMENT_FAULT, paymentId);

        Payment payment = findPendingPaymentOrThrow(paymentId);

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
        log.info(Message.PAYMENT_ERROR, payment.getOrderId());
    }

    private Payment findPendingPaymentOrThrow(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    log.warn(Message.PAYMENT_NOT_FOUND, paymentId);
                    return new NoOrderFoundException(String.format(Message.PAYMENT_NOT_FOUND_EXCEPTION, paymentId));
                });

        if (payment.getState() != PaymentState.PENDING) {
            log.warn(Message.PAYMENT_NOT_PENDING, paymentId);
            throw new IllegalStateException(Message.PAYMENT_NOT_PENDING_EXCEPTION);
        }
        return payment;
    }
}
