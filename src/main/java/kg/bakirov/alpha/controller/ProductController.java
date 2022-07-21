package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
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
    public ResponseEntity<?> products(@RequestParam("firmno") int firmNo,
                                      @RequestParam("periodno") int periodNo,
                                      @RequestParam("begdate") String begDate,
                                      @RequestParam("enddate") String endDate,
                                      @RequestParam("sourceindex") int sourceIndex
                                      ) {
        try {
            return ResponseEntity.ok(productService.getProducts(firmNo, periodNo, begDate, endDate, sourceIndex));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/inventory")
    public ResponseEntity<?> productsInventory(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(productService.getProductsInventory(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche")
    public ResponseEntity<?> productsFiche(@RequestParam("firmno") int firmNo,
                                           @RequestParam("periodno") int periodNo,
                                           @RequestParam("begdate") String begDate,
                                           @RequestParam("enddate") String endDate,
                                           @RequestParam("sourceindex") int sourceIndex) {
        try {
            return ResponseEntity.ok(productService.getProductsFiche(firmNo, periodNo, begDate, endDate, sourceIndex));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/price")
    public ResponseEntity<?> productsPrice(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(productService.getProductsPrice(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
