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
    public ResponseEntity<?> accountDebit(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(accountService.getAccountDebit(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<?> accountExtract(@RequestParam("firmno") int firmNo, @RequestParam("periodno") int periodNo) {
        try {
            return ResponseEntity.ok(accountService.getAccountExtract(firmNo, periodNo));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
