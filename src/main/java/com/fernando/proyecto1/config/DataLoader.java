package com.fernando.proyecto1.config;

import com.fernando.proyecto1.entity.*;
import com.fernando.proyecto1.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//Creo un DataLoader para tener codigos insertados preparados para testeo sin tener que hacer uno a uno
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            TerminalRepository terminalRepository,
            CategoryRepository categoryRepository,
            OrderProductRepository orderProductRepository
    ) {
        return args -> {

            // =========================
            // CATEGORÍAS
            // =========================
            CategoryEntity food = categoryRepository.save(
                    CategoryEntity.builder().name("FOOD").build()
            );

            CategoryEntity drink = categoryRepository.save(
                    CategoryEntity.builder().name("DRINK").build()
            );

            CategoryEntity dessert = categoryRepository.save(
                    CategoryEntity.builder().name("DESSERT").build()
            );

            // =========================
            // PRODUCTOS (10)
            // =========================
            List<ProductEntity> products = productRepository.saveAll(List.of(

                    ProductEntity.builder().name("Pizza").price(new BigDecimal("8.5")).category(food).build(),
                    ProductEntity.builder().name("Hamburguesa").price(new BigDecimal("6.5")).category(food).build(),
                    ProductEntity.builder().name("Patatas").price(new BigDecimal("3.5")).category(food).build(),
                    ProductEntity.builder().name("Wrap").price(new BigDecimal("5.5")).category(food).build(),
                    ProductEntity.builder().name("Ensalada").price(new BigDecimal("4.5")).category(food).build(),

                    ProductEntity.builder().name("Agua").price(new BigDecimal("1.5")).category(drink).build(),
                    ProductEntity.builder().name("Cerveza").price(new BigDecimal("2.5")).category(drink).build(),
                    ProductEntity.builder().name("Refresco").price(new BigDecimal("2.0")).category(drink).build(),

                    ProductEntity.builder().name("Helado").price(new BigDecimal("3.0")).category(dessert).build(),
                    ProductEntity.builder().name("Tarta").price(new BigDecimal("4.0")).category(dessert).build()
            ));

            // =========================
            // TERMINALES
            // =========================
            TerminalEntity barra = terminalRepository.save(
                    TerminalEntity.builder().name("Barra").build()
            );

            TerminalEntity terraza = terminalRepository.save(
                    TerminalEntity.builder().name("Terraza").build()
            );

            TerminalEntity sala = terminalRepository.save(
                    TerminalEntity.builder().name("Sala").build()
            );
            // =========================
            // PEDIDOS (opcionales de prueba)
            // =========================

            List<ProductEntity> p = products;

// =========================
// PEDIDO 1 (Barra) - hamburguesa + refresco
// =========================
            OrderEntity o1 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.COMPLETED)
                    .terminal(barra)
                    .total(BigDecimal.ZERO)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o1)
                    .product(p.get(1)) // Hamburguesa
                    .quantity(2)
                    .price(new BigDecimal("6.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o1)
                    .product(p.get(7)) // Refresco
                    .quantity(2)
                    .price(new BigDecimal("2.00"))
                    .build()
            );

            o1.setTotal(new BigDecimal("17.00"));
            orderRepository.save(o1);


// =========================
// PEDIDO 2 (Terraza) - pizza + cerveza
// =========================
            OrderEntity o2 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .terminal(terraza)
                    .total(BigDecimal.ZERO)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o2)
                    .product(p.get(0)) // Pizza
                    .quantity(1)
                    .price(new BigDecimal("8.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o2)
                    .product(p.get(6)) // Cerveza
                    .quantity(2)
                    .price(new BigDecimal("2.50"))
                    .build()
            );

            o2.setTotal(new BigDecimal("13.50"));
            orderRepository.save(o2);


// =========================
// PEDIDO 3 (Sala) - wrap + agua + ensalada
// =========================
            OrderEntity o3 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.COMPLETED)
                    .terminal(sala)
                    .total(BigDecimal.ZERO)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o3)
                    .product(p.get(3)) // Wrap
                    .quantity(1)
                    .price(new BigDecimal("5.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o3)
                    .product(p.get(5)) // Agua
                    .quantity(2)
                    .price(new BigDecimal("1.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o3)
                    .product(p.get(4)) // Ensalada
                    .quantity(1)
                    .price(new BigDecimal("4.50"))
                    .build()
            );

            o3.setTotal(new BigDecimal("13.00"));
            orderRepository.save(o3);


// =========================
// PEDIDO 4 (Barra) - patatas + cerveza + refresco
// =========================
            OrderEntity o4 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .terminal(barra)
                    .total(BigDecimal.ZERO)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o4)
                    .product(p.get(2)) // Patatas
                    .quantity(2)
                    .price(new BigDecimal("3.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o4)
                    .product(p.get(6)) // Cerveza
                    .quantity(1)
                    .price(new BigDecimal("2.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o4)
                    .product(p.get(7)) // Refresco
                    .quantity(1)
                    .price(new BigDecimal("2.00"))
                    .build()
            );

            o4.setTotal(new BigDecimal("11.50"));
            orderRepository.save(o4);


// =========================
// PEDIDO 5 (Terraza) - pizza + helado + tarta
// =========================
            OrderEntity o5 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.COMPLETED)
                    .terminal(terraza)
                    .total(BigDecimal.ZERO)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o5)
                    .product(p.get(0)) // Pizza
                    .quantity(1)
                    .price(new BigDecimal("8.50"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o5)
                    .product(p.get(8)) // Helado
                    .quantity(2)
                    .price(new BigDecimal("3.00"))
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o5)
                    .product(p.get(9)) // Tarta
                    .quantity(1)
                    .price(new BigDecimal("4.00"))
                    .build()
            );

            o5.setTotal(new BigDecimal("18.50"));
            orderRepository.save(o5);
        };
    }

    private String generateCode(){
        return UUID.randomUUID().toString().substring(0,8);
    }
}