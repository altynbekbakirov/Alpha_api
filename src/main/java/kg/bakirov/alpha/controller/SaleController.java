package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<?> sales(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSales(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/total")
    public ResponseEntity<?> salesTotal(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSalesTotal(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/month")
    public ResponseEntity<?> salesMonth(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSalesMonth(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/manager")
    public ResponseEntity<?> salesManager(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSalesManager(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client")
    public ResponseEntity<?> salesClient(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSalesClient(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/table")
    public ResponseEntity<?> salesTable(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSalesTable(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/detail")
    public ResponseEntity<?> salesDetail(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(saleService.getSalesDetail(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
