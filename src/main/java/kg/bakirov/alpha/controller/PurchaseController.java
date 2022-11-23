package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.purchases.ResponseClient;
import kg.bakirov.alpha.model.purchases.ResponseModel;
import kg.bakirov.alpha.model.purchases.ResponseTotal;
import kg.bakirov.alpha.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<?> purchases(@RequestBody ResponseClient response) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchases(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getOperationType(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("{fiche}")
    public ResponseEntity<?> getFiche(@RequestBody ResponseModel response, @PathVariable String fiche) {
        try {
            return ResponseEntity.ok(purchaseService.getFiche(response.getFirmNo(), response.getPeriodNo(), Integer.parseInt(fiche)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/total")
    public ResponseEntity<?> purchasesTotal(@RequestBody ResponseTotal response) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesTotal(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/month")
    public ResponseEntity<?> purchasesMonth(@RequestBody ResponseModel response) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesMonth(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client")
    public ResponseEntity<?> purchasesClient(@RequestBody ResponseTotal response) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesClient(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client/{code}")
    public ResponseEntity<?> purchasesClientFiches(@RequestBody ResponseModel response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchasesClientFiches(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
