package com.boostmytool.beststore.controllers;

import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) { 
		//@Valid: Annotation này hướng dẫn Spring thực hiện xác nhận đối tượng productDto trước khi xử lý yêu cầu
		//@ModelAttribute: Chú thích này ràng buộc dữ liệu từ yêu cầu HTTP vào đối tượng ProductDto và thêm đối tượng này vào model. Điều này cho phép Spring tự động gán các giá trị từ biểu mẫu vào các thuộc tính của ProductDto
		//BindingResult result: Đối tượng BindingResult chứa kết quả của quá trình xác nhận. Nó lưu giữ các lỗi (nếu có) xảy ra trong quá trình xác nhận đối tượng productDto.
		
		if(productDto.getImageFile() == null || productDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("productDto", "ImageFile", "The image file is required")); // 1: doi tuong, 2:truong du lieu, 3:thong bao loi
		}
		
		if(result.hasErrors()) {
			return "products/CreateProduct";
		}
		
		//save imageFile
		MultipartFile image = productDto.getImageFile();
		Date createAt = new Date();
		String storeFileName = image.getOriginalFilename();
		
		try(InputStream inputStream = image.getInputStream()) {
			String upLoaDir = "public/images";
			Path upLoadPath = Paths.get(upLoaDir);
			
			if(!Files.exists(upLoadPath)) {
				Files.copy(inputStream, Paths.get(upLoaDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (Exception e) {
			System.out.print("Exception" + e.getMessage());
		}
		
		Product product = new Product();
		product.setName(productDto.getName());
		product.setBrand(productDto.getBrand());
		product.setCategory(productDto.getCategory());
		product.setCreateAt(createAt);
		product.setDescription(productDto.getDescription());
		product.setPrice(productDto.getPrice());
		product.setImageFileName(storeFileName);
		
		repo.save(product);
		
		return "redirect:/products";
	}
	
	@GetMapping({"/edit"})
	public String showEditPage(Model model, @RequestParam int id) {
		//@RequestParam int id: Annotation @RequestParam ràng buộc giá trị của tham số yêu cầu HTTP vào biến id
		try {
			
			Product product = repo.findById(id).get();
			model.addAttribute("product", product);
			
			ProductDto productDto = new ProductDto();
			productDto.setName(product.getName());
			productDto.setBrand(product.getBrand());
			productDto.setCategory(product.getCategory());
			productDto.setDescription(product.getDescription());
			productDto.setPrice(product.getPrice());
			
			model.addAttribute("productDto", productDto);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return "redirect:/products";
		}
		
		return "products/EditProduct";
	}
	@PostMapping("/edit")
	public String updateProduct(
			Model model, 
			@RequestParam int id,
			@Valid @ModelAttribute ProductDto productDto, 
			BindingResult result) {
		
		try {
			Product product = repo.findById(id).get();
			model.addAttribute("product", product);
			
			if(result.hasErrors()) {
				return "products/EditProduct";
			}
			
			if(!product.getImageFileName().isEmpty() || product.getImageFileName() != null) {
				//delete old  image
				String uploadDir = "public/images";
				Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
				
				try {
					Files.delete(oldImagePath);
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
				
				//save new ImageFile
				MultipartFile image = productDto.getImageFile();
				String storeFileName = image.getOriginalFilename();
				try(InputStream inputStream = image.getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				
				product.setImageFileName(storeFileName);
			}
			
			product.setBrand(productDto.getBrand());
			product.setName(productDto.getName());
			product.setPrice(productDto.getPrice());
			product.setCategory(productDto.getCategory());
			product.setDescription(productDto.getDescription());
			repo.save(product);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		
		return "redirect:/products";
	}
	
	@GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        try {
            Product product = repo.findById(id).get();
            
            // Xóa ảnh sản phẩm nếu có
            String uploadDir = "public/images/";
            if (product.getImageFileName() != null && !product.getImageFileName().isEmpty()) {
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                Files.deleteIfExists(oldImagePath);
            }
            
            // Xóa sản phẩm từ cơ sở dữ liệu
            repo.delete(product);
        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
            // Xử lý lỗi nếu cần thiết
        }

        return "redirect:/products";
    }

}
	