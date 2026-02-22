package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String>{

    @EntityGraph(value = "Cart.withItemsAndVariants", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId")
    Optional<Cart> findByIdWithItems(String cartId);
}
