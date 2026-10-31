package com.fernando.proyecto1.service;

import com.fernando.proyecto1.dto.*;
import com.fernando.proyecto1.entity.*;
import com.fernando.proyecto1.exceptions.OrderNotFoundException;
import com.fernando.proyecto1.exceptions.TerminalNotFoundException;
import com.fernando.proyecto1.repository.OrderRepository;
import com.fernando.proyecto1.repository.ProductRepository;
import com.fernando.proyecto1.repository.OrderProductRepository;
import com.fernando.proyecto1.repository.TerminalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderService {

    private final TerminalRepository terminalRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    //Constructores
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        TerminalRepository terminalRepository,
                        OrderProductRepository orderProductRepository){

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.terminalRepository = terminalRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public OrderResponse addProduct(Long orderId, AddProductRequest request) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        OrderProductEntity existing = order.getProducts().stream()
                .filter(p -> p.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            orderProductRepository.save(existing);
        } else {
            OrderProductEntity orderProduct = OrderProductEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderProductRepository.save(orderProduct);
            order.getProducts().add(orderProduct);
        }

        double total = order.getProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        order.setTotal(total);
        orderRepository.save(order);

        return mapToResponse(order);
    }
    //Delete
    public OrderResponse removeProduct(Long orderId, Long productId){

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        boolean removed = order.getProducts().removeIf(op ->
                op.getProduct().getId().equals(productId)
        );

        if (!removed) {
            throw new RuntimeException("El producto no está en el pedido");
        }

        double total = order.getProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        order.setTotal(total);

        orderRepository.save(order);

        return mapToResponse(order);
    }

    //Pilla el producto más vendido
    public ProductStatsResponse getMostSoldProduct() {

        return orderRepository.findAll().stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        op -> op.getProduct().getName(),
                        Collectors.summingInt(OrderProductEntity::getQuantity)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> ProductStatsResponse.builder()
                        .name(entry.getKey())
                        .totalSold(entry.getValue())
                        .build())
                .orElse(null);
    }
    public ProductStatsResponse getLeastSoldProduct() {

        return orderRepository.findAll().stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        op -> op.getProduct().getName(),
                        Collectors.summingInt(OrderProductEntity::getQuantity)
                ))
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(entry -> ProductStatsResponse.builder()
                        .name(entry.getKey())
                        .totalSold(entry.getValue())
                        .build())
                .orElse(null);
    }

    public List<ProductStatsResponse> getTopProducts() {

        return orderRepository.findAll().stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        op -> op.getProduct().getName(),
                        Collectors.summingInt(OrderProductEntity::getQuantity)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(entry -> ProductStatsResponse.builder()
                        .name(entry.getKey())
                        .totalSold(entry.getValue())
                        .build())
                .toList();
    }

    public OrderResponse createOrder(OrderRequest request){

        TerminalEntity terminal = terminalRepository.findById(request.getTerminalId())
                .orElseThrow(() -> new TerminalNotFoundException("Terminal no encontrada"));
        OrderEntity order = OrderEntity.builder()
                .total(request.getTotal())
                .code(generateCode())
                .date(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .terminal(terminal)
                .build();

        OrderEntity saved = orderRepository.save(order);

        return mapToResponse(saved);
    }
    //Encontrar todos los pedidos
    public List<OrderResponse> getAllOrders(OrderStatus status){

        List<OrderEntity> orders;

        if(status != null){
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    //Encontrar solo uno por ID
    public OrderResponse getOrderById(Long id){
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado"));

        return mapToResponse(order);
    }

    //Update
    public OrderResponse updateOrder(Long id, OrderRequest request) {

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado"));

        order.setTotal(request.getTotal());

        TerminalEntity terminal = terminalRepository.findById(request.getTerminalId())
                .orElseThrow(() -> new TerminalNotFoundException("Terminal no encontrada"));

        order.setTerminal(terminal);

        OrderEntity saved = orderRepository.save(order);

        return mapToResponse(saved);
    }

    //Delete
    public void deleteOrder(Long id){

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado"));
        orderRepository.delete(order);
    }


    // Actualizar estado del pedido
    public OrderEntity updateStatus(Long id, OrderStatus status) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Pedido no encontrado"));

        OrderStatus currentStatus = order.getStatus();

        if (isFinalStatus(currentStatus)) {
            throw new RuntimeException(
                    "No se puede modificar un pedido finalizado");
        }

        if (currentStatus == status) {
            throw new RuntimeException(
                    "El pedido ya tiene ese estado");
        }

        boolean validTransition =
                (currentStatus == OrderStatus.PENDING
                        && (status == OrderStatus.IN_PREPARATION
                        || status == OrderStatus.CANCELLED))
                        || (currentStatus == OrderStatus.IN_PREPARATION
                        && (status == OrderStatus.COMPLETED
                        || status == OrderStatus.CANCELLED))
                        || (currentStatus == OrderStatus.COMPLETED
                        && status == OrderStatus.PICKED_UP);

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Transición de estado no permitida: "
                            + currentStatus + " -> " + status);
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    // Comprobar si el pedido ya ha terminado
    private boolean isFinalStatus(OrderStatus status) {
        return status == OrderStatus.CANCELLED
                || status == OrderStatus.PICKED_UP;
    }


    private OrderResponse mapToResponse(OrderEntity order){
        List<OrderProductEntity> products =
                Optional.ofNullable(order.getProducts())
                        .orElse(Collections.emptyList());

        return OrderResponse.builder()
                .id(order.getId())
                .code(order.getCode())
                .date(order.getDate())
                .total(order.getTotal())
                .status(order.getStatus().name())
                .products(
                        products.stream()
                                .filter(p -> p.getProduct() != null)
                                .map(p -> OrderProductResponse.builder()
                                        .productId(p.getProduct().getId())
                                        .name(p.getProduct().getName())
                                        .quantity(p.getQuantity())
                                        .price(p.getPrice())
                                        .build())
                                .toList()
                )
                .build();
    }
    // Generar código para identificar el pedido
    private String generateCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase(Locale.ROOT);
    }
}
