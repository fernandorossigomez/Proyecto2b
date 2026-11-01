package com.fernando.proyecto1.controller;

import com.fernando.proyecto1.dto.*;
import com.fernando.proyecto1.entity.OrderEntity;
import com.fernando.proyecto1.entity.OrderStatus;
import com.fernando.proyecto1.entity.ProductEntity;
import com.fernando.proyecto1.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Orders", description = "Gestión de pedidos")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Crear pedido",
            description = "Crea un nuevo pedido asociado a un terminal"
    )
    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/{orderId}/products")
    public OrderResponse addProduct(
            @PathVariable Long orderId,
            @Valid @RequestBody AddProductRequest request
    ) {
        return orderService.addProduct(orderId, request);
    }

    //Todos los orders
    @Operation(
            summary = "Obtener pedidos",
            description = "Devuelve todos los pedidos del sistema"
    )
    @GetMapping
    public List<OrderResponse> getAllOrders(@RequestParam(required = false) OrderStatus status) {
        return orderService.getAllOrders(status);
    }

    //Uno por ID
    @Operation(
            summary = "Obtener pedido por ID",
            description = "Devuelve un pedido específico por su identificador"
    )
    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Buscar pedido por código público
    @Operation(
            summary = "Obtener pedido por código",
            description = "Devuelve un pedido mediante su código público"
    )
    @GetMapping("/by-code/{code}")
    public OrderResponse getOrderByCode(@PathVariable String code) {
        return orderService.getOrderByCode(code);
    }

    //Update
    @Operation(
            summary = "Actualizar pedido",
            description = "Actualiza un pedido existente"
    )
    @PutMapping("/{id}")
    public OrderResponse updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest request
    ) {
        return orderService.updateOrder(id, request);
    }

    //Delete
    @Operation(
            summary = "Eliminar pedido",
            description = "Elimina un pedido por ID"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @DeleteMapping("/{orderId}/products/{productId}")
    public ResponseEntity<OrderResponse> removeProduct(
            @PathVariable Long orderId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(orderService.removeProduct(orderId, productId));
    }

    //Status
    @Operation(
            summary = "Actualizar estado del pedido",
            description = "Permite cambiar el estado del pedido siguiendo "
                    + "las transiciones válidas del flujo de trabajo"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        orderService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/most-sold")
    public ResponseEntity<List<ProductStatsResponse>> getMostSold() {
        return ResponseEntity.ok(orderService.getMostSoldProduct());
    }

    @GetMapping("/least-sold")
    public ResponseEntity<List<ProductStatsResponse>> getLeastSold() {
        return ResponseEntity.ok(orderService.getLeastSoldProduct());
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<ProductStatsResponse>> getTopProducts() {
        return ResponseEntity.ok(orderService.getTopProducts());
    }

}

