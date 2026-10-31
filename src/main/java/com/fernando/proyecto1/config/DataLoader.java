package com.fernando.proyecto1.config;

import com.fernando.proyecto1.entity.*;
import com.fernando.proyecto1.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

                    ProductEntity.builder().name("Pizza").price(8.5).category(food).build(),
                    ProductEntity.builder().name("Hamburguesa").price(6.5).category(food).build(),
                    ProductEntity.builder().name("Patatas").price(3.5).category(food).build(),
                    ProductEntity.builder().name("Wrap").price(5.5).category(food).build(),
                    ProductEntity.builder().name("Ensalada").price(4.5).category(food).build(),

                    ProductEntity.builder().name("Agua").price(1.5).category(drink).build(),
                    ProductEntity.builder().name("Cerveza").price(2.5).category(drink).build(),
                    ProductEntity.builder().name("Refresco").price(2.0).category(drink).build(),

                    ProductEntity.builder().name("Helado").price(3.0).category(dessert).build(),
                    ProductEntity.builder().name("Tarta").price(4.0).category(dessert).build()
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
                    .total(0.0)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o1)
                    .product(p.get(1)) // Hamburguesa
                    .quantity(2)
                    .price(6.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o1)
                    .product(p.get(7)) // Refresco
                    .quantity(2)
                    .price(2.0)
                    .build()
            );

            o1.setTotal((2 * 6.5) + (2 * 2.0));
            orderRepository.save(o1);


// =========================
// PEDIDO 2 (Terraza) - pizza + cerveza
// =========================
            OrderEntity o2 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .terminal(terraza)
                    .total(0.0)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o2)
                    .product(p.get(0)) // Pizza
                    .quantity(1)
                    .price(8.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o2)
                    .product(p.get(6)) // Cerveza
                    .quantity(2)
                    .price(2.5)
                    .build()
            );

            o2.setTotal(8.5 + (2 * 2.5));
            orderRepository.save(o2);


// =========================
// PEDIDO 3 (Sala) - wrap + agua + ensalada
// =========================
            OrderEntity o3 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.COMPLETED)
                    .terminal(sala)
                    .total(0.0)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o3)
                    .product(p.get(3)) // Wrap
                    .quantity(1)
                    .price(5.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o3)
                    .product(p.get(5)) // Agua
                    .quantity(2)
                    .price(1.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o3)
                    .product(p.get(4)) // Ensalada
                    .quantity(1)
                    .price(4.5)
                    .build()
            );

            o3.setTotal(5.5 + (2 * 1.5) + 4.5);
            orderRepository.save(o3);


// =========================
// PEDIDO 4 (Barra) - patatas + cerveza + refresco
// =========================
            OrderEntity o4 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .terminal(barra)
                    .total(0.0)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o4)
                    .product(p.get(2)) // Patatas
                    .quantity(2)
                    .price(3.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o4)
                    .product(p.get(6)) // Cerveza
                    .quantity(1)
                    .price(2.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o4)
                    .product(p.get(7)) // Refresco
                    .quantity(1)
                    .price(2.0)
                    .build()
            );

            o4.setTotal((2 * 3.5) + 2.5 + 2.0);
            orderRepository.save(o4);


// =========================
// PEDIDO 5 (Terraza) - pizza + helado + tarta
// =========================
            OrderEntity o5 = orderRepository.save(OrderEntity.builder()
                    .code(generateCode())
                    .date(LocalDateTime.now())
                    .status(OrderStatus.COMPLETED)
                    .terminal(terraza)
                    .total(0.0)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o5)
                    .product(p.get(0)) // Pizza
                    .quantity(1)
                    .price(8.5)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o5)
                    .product(p.get(8)) // Helado
                    .quantity(2)
                    .price(3.0)
                    .build()
            );

            orderProductRepository.save(OrderProductEntity.builder()
                    .order(o5)
                    .product(p.get(9)) // Tarta
                    .quantity(1)
                    .price(4.0)
                    .build()
            );

            o5.setTotal(8.5 + (2 * 3.0) + 4.0);
            orderRepository.save(o5);
        };
    }

    private String generateCode(){
        return UUID.randomUUID().toString().substring(0,8);
    }
}