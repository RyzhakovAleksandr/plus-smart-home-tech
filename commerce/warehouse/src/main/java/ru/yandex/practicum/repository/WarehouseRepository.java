package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.WarehouseProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProduct, UUID> {
    boolean existsByProductId(UUID productId);

    List<WarehouseProduct> findByProductIdIn(List<UUID> productIds);

    Optional<WarehouseProduct> findByProductId(UUID productId);
}
