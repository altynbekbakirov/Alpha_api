package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/")
public class MainController {

    private final MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    // COMPANY LIST
    @GetMapping
    public ResponseEntity<?> getCompanies() {
        try {
            return ResponseEntity.ok(mainService.getCompanyList());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // PERIOD LIST
    @GetMapping("/{firmNo}")
    public ResponseEntity<?> getPeriodsByFirmNo(@PathVariable("firmNo") int id) {
        try {
            return ResponseEntity.ok(mainService.getPeriodList(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    // WAREHOUSE LIST
    @GetMapping("/{firmNo}/ware")
    public ResponseEntity<?> getWareHousesByFirmNo(@PathVariable("firmNo") int id) {
        try {
            return ResponseEntity.ok(mainService.getWareList(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // NETWORK CLEAR
    @GetMapping("/{firmNo}/clear")
    public ResponseEntity<?> networkClear(@PathVariable("firmNo") int id) {
        try {
            return ResponseEntity.ok(mainService.networkClear(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
