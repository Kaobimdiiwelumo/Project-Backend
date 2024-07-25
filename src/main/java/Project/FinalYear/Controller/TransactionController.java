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
        List<String> blacklistedUsers = transactionService.getBlacklistedUsers();
        if (blacklistedUsers.contains(transactionRequest.getNameOrig()) || blacklistedUsers.contains(transactionRequest.getNameDest())) {
            return ResponseEntity.badRequest().body("Transaction blocked due to blacklisted user");
        }

        String interpretation = transactionService.processTransaction(
                transactionRequest.getType(),
                transactionRequest.getAmount(),
                transactionRequest.getOldbalanceOrg(),
                transactionRequest.getNewbalanceOrig(),
                transactionRequest.getOldbalanceDest(),
                transactionRequest.getNewbalanceDest(),
                transactionRequest.getNameOrig(),
                transactionRequest.getNameDest(),
                transactionRequest.getBankOrig(),
                transactionRequest.getBankDest()
        );

        return ResponseEntity.ok("Transaction processed successfully. Interpretation: " + interpretation);
    }
}