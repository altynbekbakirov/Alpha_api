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
            return ResponseEntity.ok(productService.getProducts(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/inventory")
    public ResponseEntity<?> productsInventory(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsInventory(response.getFirmNo(), response.getPeriodNo()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche")
    public ResponseEntity<?> productsFiche(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsFiche(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche/{fiche}")
    public ResponseEntity<?> productFiche(@RequestBody ResponseModel response, @PathVariable String fiche) {
        try {
            return ResponseEntity.ok(productService.getFiche(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), Integer.parseInt(fiche)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> productsTransactions(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductTransactions(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/transactions/{code}")
    public ResponseEntity<?> productTransactions(@RequestBody ResponseModel response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(productService.getProductTransaction(
                    response.getFirmNo(),
                    response.getPeriodNo(),
                    response.getBegDate(),
                    response.getEndDate(),
                    response.getSourceIndex(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/price")
    public ResponseEntity<?> productsPrice(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsPrice(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/prices")
    public ResponseEntity<?> productsPrices(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(productService.getProductsPrices(
                    response.getFirmNo(),
                    response.getPeriodNo(),
                    response.getBegDate(),
                    response.getEndDate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/prices/{code}")
    public ResponseEntity<?> productPrices(@RequestBody ResponseModel response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(productService.getProductPrices(
                    response.getFirmNo(),
                    response.getPeriodNo(),
                    response.getBegDate(),
                    response.getEndDate(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
