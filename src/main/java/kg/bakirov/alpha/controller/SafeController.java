package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.safes.ResponseSafe;
import kg.bakirov.alpha.service.SafeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/safes")
public class SafeController {

    private final SafeService safeService;

    public SafeController(SafeService safeService) {
        this.safeService = safeService;
    }

    @PostMapping
    public ResponseEntity<?> safes(@RequestBody ResponseSafe response) {
        try {
            return ResponseEntity.ok(safeService.getSafes(response.getFirmNo(), response.getPeriodNo()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{code}")
    public ResponseEntity<?> safeResume(@RequestBody ResponseSafe response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(safeService.getSafeResume(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<?> safesExtracts(@RequestBody ResponseSafe response) {
        try {
            return ResponseEntity.ok(safeService.getSafesExtracts(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getFilterName(), response.getOperationType()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/extract/{code}")
    public ResponseEntity<?> safeExtracts(@RequestBody ResponseSafe response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(safeService.getSafeExtracts(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), code, response.getFilterName(), response.getOperationType()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
