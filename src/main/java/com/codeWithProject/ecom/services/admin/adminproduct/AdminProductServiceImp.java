package com.codeWithProject.ecom.services.admin.adminproduct;

import com.codeWithProject.ecom.dto.ProductDto;
import com.codeWithProject.ecom.entity.Category;
import com.codeWithProject.ecom.entity.Product;
import com.codeWithProject.ecom.repository.CategoryRepository;
import com.codeWithProject.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImp implements AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductDto addProduct(ProductDto productDto){
        Product product=new Product();
        product.setName(product.getName());
        product.setDescription(product.getDescription());
        product.setPrice(product.getPrice());
        byte[] imgBytes = product.getImg();
        product.setImg(imgBytes);
          Category category=categoryRepository.findById(productDto.getCategoryId()).orElseThrow();

          product.setCategory((category));
          return productRepository.save(product).getDto();
    }

    public List<ProductDto> getAllProducts(){
        List<Product> products=productRepository.findAll();
        return  products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductByName( String name){
        List<Product> products=productRepository.findAllByNameContaining(name);
        return  products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public boolean deleteProduct(Long id){
        Optional<Product> optionalProduct=productRepository.findById(id);
        if(optionalProduct.isPresent()){
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ProductDto getProductById(Long productId){
      Optional<Product> optionalProduct=productRepository.findById(productId);

      if(optionalProduct.isPresent()){
          return optionalProduct.get().getDto();
      }else {
          return  null;
      }
    }
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Optional<Product> optionalProduct=productRepository.findById(productId);
        Optional<Category> optionalCategory= categoryRepository.findById(productDto.getCategoryId());
        if(optionalProduct.isPresent() && optionalCategory.isPresent()){
            Product product=optionalProduct.get();
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCategory(optionalCategory.get());

            if(productDto.getImg()!=null){
                try {
                    product.setImg(productDto.getImg().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return  productRepository.save(product).getDto();
        }else{
            return  null;
        }
    }
}

