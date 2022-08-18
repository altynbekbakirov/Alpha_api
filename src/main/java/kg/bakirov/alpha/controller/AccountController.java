package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.accounts.ResponseAccount;
import kg.bakirov.alpha.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> accounts(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccounts(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<?> accountDebit(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccountDebit(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<?> accountExtract(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccountExtract(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/extract/{code}")
    public ResponseEntity<?> accountExtractOne(@RequestBody ResponseAccount response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(accountService.getAccountExtract(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche")
    public ResponseEntity<?> accountFiches(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccountFiches(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche/{code}")
    public ResponseEntity<?> accountFiche(@RequestBody ResponseAccount response, @PathVariable int code) {
        try {
            return ResponseEntity.ok(accountService.getAccountFiche(response.getFirmno(), response.getPeriodno(), response.getBegdate(), response.getEnddate(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
