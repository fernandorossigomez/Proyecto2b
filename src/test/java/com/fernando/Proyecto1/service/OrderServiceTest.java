package com.fernando.Proyecto1.service;

import com.fernando.proyecto1.dto.*;
import com.fernando.proyecto1.entity.*;
import com.fernando.proyecto1.exceptions.*;
import com.fernando.proyecto1.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fernando.proyecto1.service.OrderService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TerminalRepository terminalRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderService orderService;

    // --------------------------------------------------
    // CREATE ORDER
    // --------------------------------------------------

    @Test
    void createOrderShouldSaveAndReturnOrder() {
        TerminalEntity terminal = TerminalEntity.builder()
                .id(1L)
                .name("Barra")
                .build();

        OrderRequest request = new OrderRequest();
        request.setTotal(new BigDecimal("12.50"));
        request.setTerminalId(1L);

        when(terminalRepository.findById(1L))
                .thenReturn(Optional.of(terminal));

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> {
                    OrderEntity order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        OrderResponse response = orderService.createOrder(request);

        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("12.50"), response.getTotal());
        assertEquals("PENDING", response.getStatus());
        assertNotNull(response.getCode());
        assertEquals(6, response.getCode().length());

        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void createOrderShouldThrowExceptionWhenTerminalDoesNotExist() {
        OrderRequest request = new OrderRequest();
        request.setTotal(new BigDecimal("12.50"));
        request.setTerminalId(99L);

        when(terminalRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TerminalNotFoundException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    // --------------------------------------------------
    // GET ORDERS
    // --------------------------------------------------

    @Test
    void getAllOrdersShouldReturnAllOrders() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<OrderResponse> response =
                orderService.getAllOrders(null);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("PENDING", response.get(0).getStatus());

        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrdersShouldFilterByStatus() {
        OrderEntity order = createOrder(1L, OrderStatus.COMPLETED);

        when(orderRepository.findByStatus(OrderStatus.COMPLETED))
                .thenReturn(List.of(order));

        List<OrderResponse> response =
                orderService.getAllOrders(OrderStatus.COMPLETED);

        assertEquals(1, response.size());
        assertEquals("COMPLETED", response.get(0).getStatus());

        verify(orderRepository).findByStatus(OrderStatus.COMPLETED);
        verify(orderRepository, never()).findAll();
    }

    @Test
    void getAllOrdersShouldReturnEmptyListWhenNoOrdersExist() {
        when(orderRepository.findAll())
                .thenReturn(List.of());

        List<OrderResponse> response =
                orderService.getAllOrders(null);

        assertTrue(response.isEmpty());
    }

    @Test
    void getOrderByIdShouldReturnOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertEquals(1L, response.getId());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void getOrderByIdShouldThrowExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(99L)
        );
    }

    @Test
    void getOrderByCodeShouldReturnOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);
        order.setCode("ABC123");

        when(orderRepository.findByCode("ABC123"))
                .thenReturn(Optional.of(order));

        OrderResponse response =
                orderService.getOrderByCode("ABC123");

        assertEquals("ABC123", response.getCode());
        assertEquals(1L, response.getId());
    }

    @Test
    void getOrderByCodeShouldThrowExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findByCode("XXXXXX"))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderByCode("XXXXXX")
        );
    }

    // --------------------------------------------------
    // UPDATE AND DELETE ORDER
    // --------------------------------------------------

    @Test
    void updateOrderShouldUpdateAndReturnOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        TerminalEntity terminal = TerminalEntity.builder()
                .id(2L)
                .name("Terraza")
                .build();

        OrderRequest request = new OrderRequest();
        request.setTotal(new BigDecimal("20.00"));
        request.setTerminalId(2L);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(terminalRepository.findById(2L))
                .thenReturn(Optional.of(terminal));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponse response =
                orderService.updateOrder(1L, request);

        assertEquals(new BigDecimal("20.00"), response.getTotal());
        assertEquals("Terraza", order.getTerminal().getName());

        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderShouldThrowExceptionWhenOrderDoesNotExist() {
        OrderRequest request = new OrderRequest();
        request.setTotal(new BigDecimal("20.00"));
        request.setTerminalId(2L);

        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.updateOrder(99L, request)
        );

        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderShouldThrowExceptionWhenTerminalDoesNotExist() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        OrderRequest request = new OrderRequest();
        request.setTotal(new BigDecimal("20.00"));
        request.setTerminalId(99L);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(terminalRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TerminalNotFoundException.class,
                () -> orderService.updateOrder(1L, request)
        );

        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void deleteOrderShouldDeleteExistingOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrderShouldThrowExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.deleteOrder(99L)
        );

        verify(orderRepository, never()).delete(any(OrderEntity.class));
    }

    // --------------------------------------------------
    // ADD PRODUCT
    // --------------------------------------------------

    @Test
    void addProductShouldAddProductAndUpdateTotal() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        ProductEntity product = createProduct(1L, "Pizza", "8.50");

        AddProductRequest request = new AddProductRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponse response =
                orderService.addProduct(1L, request);

        assertEquals(1, order.getProducts().size());
        assertEquals(2, order.getProducts().get(0).getQuantity());
        assertEquals(new BigDecimal("17.00"), response.getTotal());

        verify(orderProductRepository).save(any(OrderProductEntity.class));
        verify(orderRepository).save(order);
    }

    @Test
    void addProductShouldIncreaseQuantityWhenProductAlreadyExists() {
        ProductEntity product = createProduct(1L, "Pizza", "8.50");

        OrderProductEntity orderProduct = OrderProductEntity.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .price(new BigDecimal("8.50"))
                .build();

        OrderEntity order = createOrder(1L, OrderStatus.PENDING);
        order.getProducts().add(orderProduct);

        AddProductRequest request = new AddProductRequest();
        request.setProductId(1L);
        request.setQuantity(3);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponse response =
                orderService.addProduct(1L, request);

        assertEquals(5, orderProduct.getQuantity());
        assertEquals(new BigDecimal("42.50"), response.getTotal());

        verify(orderProductRepository).save(orderProduct);
        verify(orderRepository).save(order);
    }

    @Test
    void addProductShouldThrowExceptionWhenOrderDoesNotExist() {
        AddProductRequest request = new AddProductRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.addProduct(99L, request)
        );

        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void addProductShouldThrowExceptionWhenProductDoesNotExist() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        AddProductRequest request = new AddProductRequest();
        request.setProductId(99L);
        request.setQuantity(1);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.addProduct(1L, request)
        );
    }

    @Test
    void addProductShouldThrowExceptionWhenProductIsInactive() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);
        ProductEntity product = createProduct(1L, "Pizza", "8.50");
        product.setActive(false);

        AddProductRequest request = new AddProductRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.addProduct(1L, request)
        );

        verify(orderProductRepository, never())
                .save(any(OrderProductEntity.class));
    }

    @Test
    void addProductShouldNotModifyFinalizedOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.PICKED_UP);

        AddProductRequest request = new AddProductRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.addProduct(1L, request)
        );

        verify(productRepository, never()).findById(anyLong());
    }

    // --------------------------------------------------
    // REMOVE PRODUCT
    // --------------------------------------------------

    @Test
    void removeProductShouldRemoveProductAndRecalculateTotal() {
        ProductEntity pizza = createProduct(1L, "Pizza", "8.50");
        ProductEntity water = createProduct(2L, "Agua", "1.50");

        OrderProductEntity pizzaLine = OrderProductEntity.builder()
                .product(pizza)
                .quantity(2)
                .price(new BigDecimal("8.50"))
                .build();

        OrderProductEntity waterLine = OrderProductEntity.builder()
                .product(water)
                .quantity(1)
                .price(new BigDecimal("1.50"))
                .build();

        OrderEntity order = createOrder(1L, OrderStatus.PENDING);
        order.getProducts().add(pizzaLine);
        order.getProducts().add(waterLine);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponse response =
                orderService.removeProduct(1L, 2L);

        assertEquals(1, order.getProducts().size());
        assertEquals(new BigDecimal("17.00"), response.getTotal());

        verify(orderRepository).save(order);
    }

    @Test
    void removeProductShouldThrowExceptionWhenProductIsNotInOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.removeProduct(1L, 99L)
        );

        verify(orderRepository, never()).save(order);
    }

    @Test
    void removeProductShouldNotModifyFinalizedOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.CANCELLED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.removeProduct(1L, 1L)
        );

        verify(orderRepository, never()).save(order);
    }

    // --------------------------------------------------
    // UPDATE STATUS
    // --------------------------------------------------

    @Test
    void updateStatusShouldAllowPendingToInPreparation() {
        assertStatusTransition(
                OrderStatus.PENDING,
                OrderStatus.IN_PREPARATION
        );
    }

    @Test
    void updateStatusShouldAllowPendingToCancelled() {
        assertStatusTransition(
                OrderStatus.PENDING,
                OrderStatus.CANCELLED
        );
    }

    @Test
    void updateStatusShouldAllowInPreparationToCompleted() {
        assertStatusTransition(
                OrderStatus.IN_PREPARATION,
                OrderStatus.COMPLETED
        );
    }

    @Test
    void updateStatusShouldAllowInPreparationToCancelled() {
        assertStatusTransition(
                OrderStatus.IN_PREPARATION,
                OrderStatus.CANCELLED
        );
    }

    @Test
    void updateStatusShouldAllowCompletedToPickedUp() {
        assertStatusTransition(
                OrderStatus.COMPLETED,
                OrderStatus.PICKED_UP
        );
    }

    @Test
    void updateStatusShouldRejectInvalidTransition() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateStatus(
                        1L,
                        OrderStatus.COMPLETED
                )
        );

        verify(orderRepository, never()).save(order);
    }

    @Test
    void updateStatusShouldRejectSameStatus() {
        OrderEntity order = createOrder(1L, OrderStatus.PENDING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateStatus(
                        1L,
                        OrderStatus.PENDING
                )
        );

        verify(orderRepository, never()).save(order);
    }

    @Test
    void updateStatusShouldRejectFinalizedOrder() {
        OrderEntity order = createOrder(1L, OrderStatus.CANCELLED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateStatus(
                        1L,
                        OrderStatus.IN_PREPARATION
                )
        );

        verify(orderRepository, never()).save(order);
    }

    @Test
    void updateStatusShouldThrowExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.updateStatus(
                        99L,
                        OrderStatus.IN_PREPARATION
                )
        );
    }

    // --------------------------------------------------
    // PRODUCT STATISTICS
    // --------------------------------------------------

    @Test
    void getMostSoldProductShouldReturnProductWithHighestSales() {
        ProductEntity pizza = createProduct(1L, "Pizza", "8.50");
        ProductEntity water = createProduct(2L, "Agua", "1.50");

        OrderEntity order = createOrder(1L, OrderStatus.COMPLETED);
        order.getProducts().add(createOrderProduct(pizza, 5));
        order.getProducts().add(createOrderProduct(water, 2));

        when(productRepository.findAll())
                .thenReturn(List.of(pizza, water));

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<ProductStatsResponse> response =
                orderService.getMostSoldProduct();

        assertEquals(1, response.size());
        assertEquals("Pizza", response.get(0).getName());
        assertEquals(5, response.get(0).getTotalSold());
    }

    @Test
    void getLeastSoldProductShouldReturnProductWithLowestSales() {
        ProductEntity pizza = createProduct(1L, "Pizza", "8.50");
        ProductEntity water = createProduct(2L, "Agua", "1.50");

        OrderEntity order = createOrder(1L, OrderStatus.COMPLETED);
        order.getProducts().add(createOrderProduct(pizza, 5));

        when(productRepository.findAll())
                .thenReturn(List.of(pizza, water));

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<ProductStatsResponse> response =
                orderService.getLeastSoldProduct();

        assertEquals(1, response.size());
        assertEquals("Agua", response.get(0).getName());
        assertEquals(0, response.get(0).getTotalSold());
    }

    @Test
    void getTopProductsShouldReturnThreeBestSellingProducts() {
        ProductEntity pizza = createProduct(1L, "Pizza", "8.50");
        ProductEntity water = createProduct(2L, "Agua", "1.50");
        ProductEntity burger = createProduct(3L, "Hamburguesa", "6.50");
        ProductEntity salad = createProduct(4L, "Ensalada", "4.50");

        OrderEntity order = createOrder(1L, OrderStatus.COMPLETED);
        order.getProducts().add(createOrderProduct(pizza, 5));
        order.getProducts().add(createOrderProduct(water, 4));
        order.getProducts().add(createOrderProduct(burger, 3));

        when(productRepository.findAll())
                .thenReturn(List.of(pizza, water, burger, salad));

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<ProductStatsResponse> response =
                orderService.getTopProducts();

        assertEquals(3, response.size());

        assertTrue(response.stream()
                .anyMatch(p -> p.getName().equals("Pizza")));

        assertTrue(response.stream()
                .anyMatch(p -> p.getName().equals("Agua")));

        assertTrue(response.stream()
                .anyMatch(p -> p.getName().equals("Hamburguesa")));
    }

    @Test
    void statisticsShouldIgnorePendingOrders() {
        ProductEntity pizza = createProduct(1L, "Pizza", "8.50");

        OrderEntity order = createOrder(1L, OrderStatus.PENDING);
        order.getProducts().add(createOrderProduct(pizza, 5));

        when(productRepository.findAll())
                .thenReturn(List.of(pizza));

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<ProductStatsResponse> response =
                orderService.getMostSoldProduct();

        assertEquals(1, response.size());
        assertEquals("Pizza", response.get(0).getName());
        assertEquals(0, response.get(0).getTotalSold());
    }

    // --------------------------------------------------
    // HELPER METHODS
    // --------------------------------------------------

    private OrderEntity createOrder(Long id, OrderStatus status) {
        return OrderEntity.builder()
                .id(id)
                .code("ABC123")
                .date(LocalDateTime.now())
                .total(BigDecimal.ZERO)
                .status(status)
                .products(new ArrayList<>())
                .build();
    }

    private ProductEntity createProduct(
            Long id,
            String name,
            String price
    ) {
        return ProductEntity.builder()
                .id(id)
                .name(name)
                .price(new BigDecimal(price))
                .active(true)
                .build();
    }

    private OrderProductEntity createOrderProduct(
            ProductEntity product,
            int quantity
    ) {
        return OrderProductEntity.builder()
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
    }

    private void assertStatusTransition(
            OrderStatus initialStatus,
            OrderStatus newStatus
    ) {
        OrderEntity order = createOrder(1L, initialStatus);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderEntity response =
                orderService.updateStatus(1L, newStatus);

        assertEquals(newStatus, response.getStatus());
        verify(orderRepository).save(order);
    }
}
