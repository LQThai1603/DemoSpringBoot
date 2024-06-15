package com.boostmytool.beststore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.models.ProductDto;
import com.boostmytool.beststore.services.ProductsRepository;

import jakarta.validation.Valid;

@Controller //Annotation này đánh dấu lớp ProductsController là một controller trong Spring MVC, nghĩa là lớp này sẽ xử lý các yêu cầu từ người dùng.
@RequestMapping("/products") //Annotation này ánh xạ tất cả các yêu cầu HTTP có đường dẫn /products đến các phương thức trong ProductsController.
public class ProductsController {
	@Autowired // Annotation này dùng để tự động tiêm đối tượng ProductsRepository vào ProductsController. Spring sẽ tự động cung cấp một đối tượng của ProductsRepository đã được cấu hình.
	private ProductsRepository repo;
	
	@GetMapping({"", "/"}) //Annotation này ánh xạ các yêu cầu HTTP GET đến phương thức showProductList. Điều này có nghĩa là khi người dùng truy cập vào URL /products bằng phương thức GET, phương thức này sẽ được gọi.
	public String showProductList(Model model) { //Tham số model của phương thức là một đối tượng Model được Spring MVC cung cấp. Đối tượng này được sử dụng để thêm các thuộc tính (attributes) mà ta muốn chuyển tới view (giao diện).
		List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id")); //Lệnh này lấy tất cả các sản phẩm từ kho dữ liệu (repository) bằng cách gọi phương thức findAll của repo, là đối tượng ProductsRepository. Kết quả là một danh sách các đối tượng Product.
		model.addAttribute("products", products); //Lệnh này thêm danh sách sản phẩm (products) vào model với tên là "products". Dữ liệu này sẽ được truy cập trong view (giao diện) bằng tên "products".
		return "products/index"; //Cuối cùng, phương thức trả về tên của view (giao diện) là products/index. Spring MVC sẽ kết hợp tên này với cấu hình của view resolver để tìm tệp HTML tương ứng, chẳng hạn như products/index.html, và trả về tệp này cho người dùng.
	}
	
	@GetMapping({"/create"}) //Annotation này ánh xạ các yêu cầu HTTP GET đến phương thức showProductList. Điều này có nghĩa là khi người dùng truy cập vào URL /products bằng phương thức GET, phương thức này sẽ được gọi.
	public String showCreatePage(Model model) { //Tham số model của phương thức là một đối tượng Model được Spring MVC cung cấp. Đối tượng này được sử dụng để thêm các thuộc tính (attributes) mà ta muốn chuyển tới view (giao diện).
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto); //Lệnh này thêm danh sách sản phẩm (products) vào model với tên là "products". Dữ liệu này sẽ được truy cập trong view (giao diện) bằng tên "products".
		return "products/CreateProduct"; //Cuối cùng, phương thức trả về tên của view (giao diện) là products/index. Spring MVC sẽ kết hợp tên này với cấu hình của view resolver để tìm tệp HTML tương ứng, chẳng hạn như products/index.html, và trả về tệp này cho người dùng.
	}

	@PostMapping({"/create"}) 
	public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) { //@Valid: Annotation này hướng dẫn Spring thực hiện xác nhận đối tượng productDto trước khi xử lý yêu cầu
		return "/redirect:/products";
	}
}
	