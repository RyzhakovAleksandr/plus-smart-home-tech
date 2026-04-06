package ru.yandex.practicum.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    UUID cartId;

    String username;

    @Column(name = "is_active")
    Boolean isActive = true;

    @OneToMany(mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    List<CartProduct> products = new ArrayList<>();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Cart)) return false;
        return cartId != null && cartId.equals(((Cart) object).getCartId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
