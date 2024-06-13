package com.boostmytool.beststore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boostmytool.beststore.services.ProductsRepository;

@Controller //dat atnotate la controller vi can vao phan nay tu URL
@RequestMapping("/products") //anh xa den thu muc products
public class ProductsController {
	@Autowired //vi can request no tu service ccontainer
	private ProductsRepository repo;
	
}
