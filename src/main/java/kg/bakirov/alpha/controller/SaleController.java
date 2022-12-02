package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.purchases.ResponseModel;
import kg.bakirov.alpha.model.sales.ResponseClient;
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
    public ResponseEntity<?> sales(@RequestBody ResponseClient response) {
        try {
            return ResponseEntity.ok(saleService.getSales(response.getFirmNo(), response.getPeriodNo(),
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
            return ResponseEntity.ok(saleService.getFiche(response.getFirmNo(), response.getPeriodNo(), Integer.parseInt(fiche)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/total")
    public ResponseEntity<?> salesTotal(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getSalesTotal(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(),
                    response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/month")
    public ResponseEntity<?> salesMonth(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesMonth(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/daily")
    public ResponseEntity<?> salesDaily(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesDaily(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/manager")
    public ResponseEntity<?> salesManager(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getSalesManager(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/manager/{code}")
    public ResponseEntity<?> salesManager(@RequestBody ResponseSale response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(saleService.getSalesManagerOne(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/ware")
    public ResponseEntity<?> salesWares(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getWareTotals(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client")
    public ResponseEntity<?> salesClient(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getSalesClient(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client/{code}")
    public ResponseEntity<?> salesClientFiches(@RequestBody ResponseSale response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(saleService.getSalesClientFiches(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/client/top")
    public ResponseEntity<?> salesClientTop(@RequestBody ResponseSale response) {
        try {
            return ResponseEntity.ok(saleService.getSalesClientTop(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), response.getSourceIndex()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/table")
    public ResponseEntity<?> salesTable(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getSalesTable(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/detail")
    public ResponseEntity<?> salesDetail(@RequestBody ResponseSale1 response) {
        try {
            return ResponseEntity.ok(saleService.getSalesDetail(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(),
                    response.getEndDate(), response.getSourceIndex(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
