package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.purchases.ResponseModel;
import kg.bakirov.alpha.model.sales.ResponseSale;
import kg.bakirov.alpha.model.sales.ResponseSale1;
import kg.bakirov.alpha.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<?> sales(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSales(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("{fiche}")
    public ResponseEntity<?> getFiche(@RequestBody ResponseModel response, @PathVariable String fiche) {
        try {
            return ResponseEntity.ok(saleService.getFiche(response.getFirmno(), response.getPeriodno(), Integer.parseInt(fiche)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/total")
    public ResponseEntity<?> salesTotal(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getSalesTotal(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/month")
    public ResponseEntity<?> salesMonth(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesMonth(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/daily")
    public ResponseEntity<?> salesDaily(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesDaily(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/manager")
    public ResponseEntity<?> salesManager(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesManager(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/manager/{code}")
    public ResponseEntity<?> salesManager(@RequestBody ResponseSale response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(saleService.getSalesManagerOne(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client")
    public ResponseEntity<?> salesClient(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesClient(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client/top")
    public ResponseEntity<?> salesClientTop(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesClientTop(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/table")
    public ResponseEntity<?> salesTable(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesTable(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/detail")
    public ResponseEntity<?> salesDetail(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesDetail(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), response.getSourceindex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
