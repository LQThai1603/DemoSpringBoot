package com.boostmytool.beststore.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.boostmytool.beststore.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer> { //integer la id
	Page<Product> findAll(Pageable pageable);
}
