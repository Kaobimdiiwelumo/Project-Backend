package Project.FinalYear.Controller;
import Project.FinalYear.DTO.TransactionDTO;
import Project.FinalYear.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/process")
    public ResponseEntity<String> processTransaction(@RequestBody TransactionDTO transactionRequest) {
        // Check if the nameOrig or nameDest is blacklisted
        List<String> blacklistedUsers = transactionService.getBlacklistedUsers();
        if (blacklistedUsers.contains(transactionRequest.getNameOrig()) || blacklistedUsers.contains(transactionRequest.getNameDest())) {
            // Return a response indicating transaction blocked
            return ResponseEntity.badRequest().body("Transaction blocked due to blacklisted user");
        }

        // Proceed with processing the transaction
        transactionService.processTransaction(
                transactionRequest.getType(),
                transactionRequest.getAmount(),
                transactionRequest.getOldbalanceOrg(),
                transactionRequest.getNewbalanceOrig(),
                transactionRequest.getOldbalanceDest(),
                transactionRequest.getNewbalanceDest(),
                transactionRequest.getNameOrig(),
                transactionRequest.getNameDest()
        );

        // Return a response indicating success
        return ResponseEntity.ok("Transaction processed successfully");
    }

}
