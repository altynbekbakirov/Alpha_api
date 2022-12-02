package kg.bakirov.alpha.controller;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.accounts.ResponseAccount;
import kg.bakirov.alpha.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            return ResponseEntity.ok(accountService.getAccounts(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<?> accountDebit(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccountDebit(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<?> accountExtract(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccountExtract(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/extract/{code}")
    public ResponseEntity<?> accountExtractOne(@RequestBody ResponseAccount response, @PathVariable String code) {
        try {
            return ResponseEntity.ok(accountService.getAccountExtract(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche")
    public ResponseEntity<?> accountFiches(@RequestBody ResponseAccount response) {
        try {
            return ResponseEntity.ok(accountService.getAccountFiches(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fiche/{code}")
    public ResponseEntity<?> accountFiche(@RequestBody ResponseAccount response, @PathVariable int code) {
        try {
            return ResponseEntity.ok(accountService.getAccountFiche(response.getFirmNo(), response.getPeriodNo(), response.getBegDate(), response.getEndDate(), code));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/aging")
    public ResponseEntity<?> accountDebitAging(@RequestBody ResponseAccount response) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date currentDate;
            Date dateEnd = new SimpleDateFormat("dd.MM.yyyy").parse(response.getEndDate());
            Calendar c = Calendar.getInstance();

            if (c.getWeekYear() > (dateEnd.getYear() + 1900)) {
                currentDate = dateEnd;
            } else {
                currentDate = new Date();
            }

            String date5 = dateFormat.format(currentDate);

            c.setTime(currentDate);
            c.add(Calendar.DATE, -30);
            Date currentDatePlusOne = c.getTime();
            String date4 = dateFormat.format(currentDatePlusOne);

            c.add(Calendar.DATE, -30);
            currentDatePlusOne = c.getTime();
            String date3 = dateFormat.format(currentDatePlusOne);

            c.add(Calendar.DATE, -30);
            currentDatePlusOne = c.getTime();
            String date2 = dateFormat.format(currentDatePlusOne);

            c.add(Calendar.DATE, -30);
            currentDatePlusOne = c.getTime();
            String date1 = dateFormat.format(currentDatePlusOne);

            return ResponseEntity.ok(accountService.getAccountsAging(response.getFirmNo(), response.getPeriodNo(),
                    response.getBegDate(), response.getEndDate(), date1, date2, date3, date4, date5, response.getFilterName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
