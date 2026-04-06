package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "cart_products")
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @ToString.Exclude
    Cart cart;

    @Column(name = "product_id", nullable = false)
    UUID productId;

    @Column(nullable = false)
    Long quantity;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CartProduct)) return false;
        return id != null && id.equals(((CartProduct) object).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
