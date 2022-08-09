package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.products.ResponseModel;
import kg.bakirov.alpha.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> products(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProducts(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/inventory")
    public ResponseEntity<?> productsInventory(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsInventory(response.getFirmno(), response.getPeriodno()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche")
    public ResponseEntity<?> productsFiche(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsFiche(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/price")
    public ResponseEntity<?> productsPrice(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsPrice(response.getFirmno(), response.getPeriodno()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
