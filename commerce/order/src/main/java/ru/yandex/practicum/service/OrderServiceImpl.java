package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getUserOrders(String username) {
        log.info(Message.GETTING_ORDER, username);
        if (username == null || username.isEmpty()) {
            log.warn(Message.NO_CORRECT_USERNAME, username);
            throw new NotAuthorizedUserException(Message.NO_CORRECT_USERNAME_EXCEPTION);
        }

        List<Order> orders = orderRepository.findAllByUsername(username);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info(Message.CREATE_NEW_ORDER, request.getUsername());

        BookedProductsDto bookedProducts =
                warehouseClient.checkProductAvailability(request.getShoppingCartDto());

        Order order = createOrderEntity(request, bookedProducts);
        List<OrderProduct> orderProducts = getOrderProducts(request, order);

        order.setProducts(orderProducts);
        orderRepository.save(order);

        DeliveryDto deliveryRequest = createDeliveryDto(order.getOrderId(), request.getDeliveryAddress());
        DeliveryDto delivery = deliveryClient.planDelivery(deliveryRequest);

        order.setDeliveryId(delivery.getDeliveryId());
        orderRepository.save(order);

        log.info(Message.CREATED_ORDERS_OK, order.getOrderId());

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info(Message.RETURN_ORDER, request.getOrderId());

        Order order = getOrderEntity(request.getOrderId());

        warehouseClient.acceptReturn(request.getProducts());

        order.setState(OrderState.PRODUCT_RETURNED);
        Order savedOrder = orderRepository.save(order);

        log.info(Message.CHANGE_ORDER, savedOrder.getOrderId(), savedOrder.getState());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {
        log.info(Message.WORK_PAYMENT_ORDER, orderId);

        Order order = getOrderEntity(orderId);

        if (order.getState() != OrderState.ASSEMBLED) {
            log.warn(Message.ORDER_NOT_ASSEMBLED_STATE, orderId, order.getState());
            throw new IllegalStateException(String.format(Message.ORDER_NOT_ASSEMBLED_EXCEPTION, order.getState()));
        }

        OrderDto orderDto = orderMapper.toDto(order);

        Double deliveryPrice = deliveryClient.deliveryCost(orderDto);
        Double productPrice = paymentClient.productsCost(orderDto);

        orderDto.setDeliveryPrice(deliveryPrice);
        orderDto.setProductPrice(productPrice);

        PaymentDto payment = paymentClient.payment(orderDto);

        order.setPaymentId(payment.getPaymentId());
        order.setProductPrice(productPrice);
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(payment.getTotalPayment());
        order.setState(OrderState.ON_PAYMENT);

        Order savedOrder = orderRepository.save(order);

        log.info(Message.INITIAL_PAYMENT, savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto paymentSuccess(UUID orderId) {
        log.info(Message.MESSAGE_PAYMENT_OK, orderId);

        Order order = getOrderEntity(orderId);

        if (order.getState() != OrderState.ON_PAYMENT) {
            log.warn(Message.ORDER_NOT_ON_PAYMENT_STATE, orderId, order.getState());

            if (order.getState() == OrderState.PAID) {
                return orderMapper.toDto(order);
            }

            throw new IllegalStateException(String.format(Message.ORDER_NO_PAYMENT_EXCEPTION, order.getState()));
        }

        order.setState(OrderState.PAID);
        Order savedOrder = orderRepository.save(order);

        log.info(Message.CHANGE_ORDER, savedOrder.getOrderId(), savedOrder.getState());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info(Message.MESSAGE_PAYMENT_FAULT, orderId);

        Order order = getOrderEntity(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info(Message.ORDER_GO_DELIVERY, orderId);

        Order order = getOrderEntity(orderId);

        if (order.getState() != OrderState.PAID) {
            log.warn(Message.DELIVERY_NO_POSSIBLE, order.getState());
            throw new IllegalStateException(String.format(Message.DELIVERY_NO_POSSIBLE_EXCEPTION, order.getState()));
        }

        deliveryClient.pickedDelivery(orderId);

        order.setState(OrderState.ON_DELIVERY);
        Order savedOrder = orderRepository.save(order);

        log.info(Message.CHANGE_ORDER, savedOrder.getOrderId(), savedOrder.getState());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto deliverySuccess(UUID orderId) {
        log.info(Message.INFO_DELIVERY_SUCCESS, orderId);

        Order order = getOrderEntity(orderId);

        if (order.getState() != OrderState.ON_DELIVERY) {
            log.warn(Message.ORDER_NOT_DELIVERY_STATE, orderId, order.getState());
            throw new IllegalStateException(String.format(Message.ORDER_NOT_DELIVERY_STATE_EXCEPTION, order.getState()));
        }

        order.setState(OrderState.DELIVERED);
        Order savedOrder = orderRepository.save(order);

        log.info(Message.DELIVERY_SUCCESS, savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info(Message.INFO_DELIVERY_FAILED, orderId);

        Order order = getOrderEntity(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        log.info(Message.DELIVERY_COMPLETE, orderId);

        Order order = getOrderEntity(orderId);

        if (order.getState() != OrderState.PAID && order.getState() != OrderState.DELIVERED) {
            log.warn(Message.ORDER_NO_CORRECT_STATE, orderId, order.getState());
            throw new IllegalStateException(String.format(Message.ORDER_NO_CORRECT_STATE_EXCEPTION, order.getState()));
        }

        order.setState(OrderState.COMPLETED);
        Order savedOrder = orderRepository.save(order);

        log.info(Message.DELIVERY_COMPLETE_OK, savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        log.info(Message.CALCULATE_TOTAL_SUM, orderId);

        Order order = getOrderEntity(orderId);
        OrderDto orderDto = orderMapper.toDto(order);

        Double totalCost = paymentClient.getTotalCost(orderDto);
        orderDto.setTotalPrice(totalCost);

        return orderDto;
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        log.info(Message.CALCULATE_TOTAL_DELIVERY, orderId);

        Order order = getOrderEntity(orderId);
        OrderDto orderDto = orderMapper.toDto(order);

        Double deliveryPrice = deliveryClient.deliveryCost(orderDto);
        orderDto.setDeliveryPrice(deliveryPrice);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info(Message.ASSEMBLY_ORDER, orderId);

        Order order = getOrderEntity(orderId);

        if (order.getState() != OrderState.NEW) {
            log.warn(Message.ORDER_CANT_ASSEMBLY_STATE, order.getState());
            throw new IllegalStateException(String.format(Message.ORDER_CANT_ASSEMBLY_STATE_EXCEPTION, order.getState()));
        }

        Map<UUID, Long> products = orderMapper.productsToMap(order.getProducts());

        warehouseClient.assemblyProductForOrderFromShoppingCart(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(orderId)
                        .products(products)
                        .build());

        log.info(Message.PRODUCT_ASSEMBLED, order.getOrderId());

        order.setState(OrderState.ASSEMBLED);
        Order savedOrder = orderRepository.save(order);

        log.info(Message.PRODUCT_ADDED_TO_ORDER, savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info(Message.PRODUCT_ADDED_TO_ORDER_FAULT, orderId);

        Order order = getOrderEntity(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    private Order getOrderEntity(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn(Message.ORDER_NOT_FOUND, orderId);
                    return new NoOrderFoundException(String.format(Message.ORDER_NOT_FOUND_EXCEPTION, orderId));
                });
    }

    private Order createOrderEntity(CreateNewOrderRequest request, BookedProductsDto bookedProducts) {
        return Order.builder()
                .username(request.getUsername())
                .shoppingCartId(request.getShoppingCartDto().getShoppingCartId())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .fragile(bookedProducts.getFragile())
                .build();
    }

    private List<OrderProduct> getOrderProducts(CreateNewOrderRequest request, Order order) {
        Map<UUID, Long> requestProducts = request.getShoppingCartDto().getProducts();
        return requestProducts.entrySet().stream()
                .map(entry -> OrderProduct.builder()
                        .order(order)
                        .productId(entry.getKey())
                        .quantity(entry.getValue())
                        .build())
                .toList();
    }

    private DeliveryDto createDeliveryDto(UUID orderId, AddressDto addressDto) {
        return DeliveryDto.builder()
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(addressDto)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();
    }
}
