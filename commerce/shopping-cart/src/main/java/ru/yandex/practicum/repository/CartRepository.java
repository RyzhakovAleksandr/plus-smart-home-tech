package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUsername(String username);

    Optional<Cart> findByUsernameAndIsActiveTrue(String username);

    @Modifying
    @Query("UPDATE Cart c SET c.isActive = false WHERE c.username = :username")
    int deactivateCart(String username);

    Optional<Cart> findByCartId(UUID cartId);
}
