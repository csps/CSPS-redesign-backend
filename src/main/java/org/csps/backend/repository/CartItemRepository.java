package org.csps.backend.repository;

import org.csps.backend.domain.entities.CartItem;
import org.csps.backend.domain.entities.composites.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId>{

}
