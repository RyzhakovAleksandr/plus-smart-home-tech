package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.CartProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {
    @Modifying
    @Query("DELETE FROM CartProduct cp WHERE cp.cart.cartId = :cartId AND cp.productId in :productIds")
    int deleteByCartIdAndProductIds(@Param("cartId") UUID cartId,
                                    @Param("productIds") List<UUID> productIds);

    Optional<CartProduct> findByCart_CartIdAndProductId(UUID cartId, UUID productId);
}
