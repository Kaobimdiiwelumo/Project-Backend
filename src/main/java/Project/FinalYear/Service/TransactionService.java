package Project.FinalYear.Service;

import Project.FinalYear.DTO.TransactionDTO;
import Project.FinalYear.Entity.Transactions;
import Project.FinalYear.Repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<String> getBlacklistedUsers() {
        // Call the repository method to fetch blacklisted users from the database
        return transactionRepository.findBlacklistedUsers();
    }


    private TransactionDTO createTransaction(String type, double amount, double oldbalanceOrg, double newbalanceOrig, double oldbalanceDest, double newbalanceDest, String nameOrig, String nameDest) {
        Transactions transaction = new Transactions();

//        int typeValue = convertTypeToNumber(type);
//        transaction.setType(String.valueOf(typeValue));
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setOldbalanceOrg(oldbalanceOrg);
        transaction.setNewbalanceOrig(newbalanceOrig);
        transaction.setOldbalanceDest(oldbalanceDest);
        transaction.setNewbalanceDest(newbalanceDest);
        transaction.setNameOrig(nameOrig);
        transaction.setNameDest(nameDest);

        TransactionDTO transactionDTO = new TransactionDTO(transaction.getType(),transaction.getAmount(), transaction.getOldbalanceOrg(), transaction.getNewbalanceOrig(), transaction.getNewbalanceOrig(),transaction.getNewbalanceDest(), transaction.getNameOrig(), transaction.getNameDest() );

        // Save the transaction to the database (if needed,
        transaction = transactionRepository.save(transaction);

        return transactionDTO;
    }
    private void sendTransactionToFlaskAPI(TransactionDTO transactionDTO) {
        // Specify the Flask API endpoint
        String apiEndpoint = "http://localhost:5000/predict";

        // Create a RestTemplate to send an HTTP POST request
        RestTemplate restTemplate = new RestTemplate();

        // Prepare the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare the payload using the transaction details
        String payload = preparePayload(transactionDTO);

        // Create a HttpEntity with headers and payload
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        // Send the HTTP POST request and receive the response
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                apiEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Extract the response body from ResponseEntity
        String response = responseEntity.getBody();

        // Process the response as needed
        System.out.println("Prediction received from Flask API: " + response);

        try {
            // Assuming 'response' is the string containing the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // Extract the "prediction" field
            JsonNode predictionNode = jsonNode.get("prediction");

            if (predictionNode != null && predictionNode.isInt()) {
                     // Extract the predicted label from the integer value
                int predictedLabel = predictionNode.asInt();

                // Interpret the predicted label
                String interpretation = (predictedLabel == 0) ? "Not Fraud" : "Fraud";
                System.out.println("Prediction Label: " + interpretation);
            } else {
                System.out.println("Error: Unable to extract valid prediction label from the response.");
            }
        } catch (JsonProcessingException e) {
            // Handle JSON parsing exception
            e.printStackTrace();
        }
    }



    private String preparePayload(TransactionDTO transactionDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(transactionDTO);
        } catch (JsonProcessingException e) {
            // Handle the exception
            e.printStackTrace();
            return null;
        }
    }

    public void processTransaction(String type, double amount, double oldbalanceOrg, double newbalanceOrig, double oldbalanceDest, double newbalanceDest,String nameOrig, String nameDest) {
        TransactionDTO transactionDTO = createTransaction(type, amount, oldbalanceOrg, newbalanceOrig, oldbalanceDest, newbalanceDest, nameOrig, nameDest);
        sendTransactionToFlaskAPI(transactionDTO);
    }
}
