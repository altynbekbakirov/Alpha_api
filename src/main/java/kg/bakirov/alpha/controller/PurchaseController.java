package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<?> purchases(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchases(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/total")
    public ResponseEntity<?> purchasesTotal(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesTotal(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/month")
    public ResponseEntity<?> purchasesMonth(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesMonth(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client")
    public ResponseEntity<?> purchasesClient(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesClient(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
