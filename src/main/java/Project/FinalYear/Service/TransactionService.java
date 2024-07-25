package Project.FinalYear.Service;
import Project.FinalYear.DTO.TransactionDTO;
import Project.FinalYear.Entity.Transactions;
import Project.FinalYear.Repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    public List<String> getBlacklistedUsers() {
        // Call the repository method to fetch blacklisted users from the database
        return transactionRepository.findBlacklistedUsers();
    }
    private TransactionDTO createTransaction(String type, double amount, double oldbalanceOrg, double newbalanceOrig, double oldbalanceDest, double newbalanceDest, String nameOrig, String nameDest, String bankOrig,String bankDest ) {
        Transactions transaction = new Transactions();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setOldbalanceOrg(oldbalanceOrg);
        transaction.setNewbalanceOrig(newbalanceOrig);
        transaction.setOldbalanceDest(oldbalanceDest);
        transaction.setNewbalanceDest(newbalanceDest);
        transaction.setNameOrig(nameOrig);
        transaction.setNameDest(nameDest);
        transaction.setBankOrig(bankOrig);
        transaction.setBankDest(bankDest);
        TransactionDTO transactionDTO = new TransactionDTO(transaction.getType(),transaction.getAmount(), transaction.getOldbalanceOrg(), transaction.getNewbalanceOrig(), transaction.getNewbalanceOrig(),transaction.getNewbalanceDest(), transaction.getNameOrig(), transaction.getNameDest(), transaction.getBankOrig(),transaction.getBankDest() );
        // Save the transaction to the database (if needed,
        transaction = transactionRepository.save(transaction);
        return transactionDTO;
    }
    private String sendTransactionToFlaskAPI(TransactionDTO transactionDTO) {
        String apiEndpoint = "http://localhost:5001/predict";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String payload = preparePayload(transactionDTO);
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                apiEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String response = responseEntity.getBody();
        System.out.println("Prediction received from Flask API: " + response);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode predictionNode = jsonNode.get("prediction");
            if (predictionNode != null && predictionNode.isInt()) {
                int predictedLabel = predictionNode.asInt();
                String interpretation = (predictedLabel == 0) ? "Not Fraud" : "Possible Fraud";
                System.out.println("Prediction Label: " + interpretation);
                publishInterpretation(interpretation, transactionDTO);
                return interpretation;
            } else {
                System.out.println("Error: Unable to extract valid prediction label from the response.");
                return "Error: Unable to extract valid prediction label from the response.";
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error: JSON processing exception.";
        }
    }
    private void publishInterpretation(String interpretation, TransactionDTO transactionDTO) {
        String clientId = MqttClient.generateClientId();
        String topic1 = "mqtt/" + transactionDTO.getBankOrig();
        String topic2 = "mqtt/" + transactionDTO.getBankDest();
        int qos = 0;
        Set<String> sentTopics = new HashSet<>();
        if (interpretation == "Possible Fraud") {
            try {
                final MqttClient client = new MqttClient("tcp://localhost:1883", clientId, new MemoryPersistence());
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName("");
                options.setPassword("".toCharArray());
                client.connect(options);
                System.out.println("Connected to MQTT broker...");
                // Construct the message content with interpretation and transaction information
                StringBuilder messageContent = new StringBuilder();
                messageContent.append(interpretation).append("\n");
                messageContent.append("Type: ").append(transactionDTO.getType()).append("\n");
                messageContent.append("Amount: ").append(transactionDTO.getAmount()).append("\n");
                messageContent.append("Old Balance Org: ").append(transactionDTO.getOldbalanceOrg()).append("\n");
                messageContent.append("New Balance Orig: ").append(transactionDTO.getNewbalanceOrig()).append("\n");
                messageContent.append("Old Balance Dest: ").append(transactionDTO.getOldbalanceDest()).append("\n");
                messageContent.append("New Balance Dest: ").append(transactionDTO.getNewbalanceDest()).append("\n");
                messageContent.append("Name Orig: ").append(transactionDTO.getNameOrig()).append("\n");
                messageContent.append("Name Dest: ").append(transactionDTO.getNameDest());
                MqttMessage message = new MqttMessage(messageContent.toString().getBytes());
                message.setQos(qos);
                // Publish to the first topic if not already sent
                if (!sentTopics.contains(topic1)) {
                    client.publish(topic1, message);
                    System.out.println("Interpretation published to topic 1: " + topic1);
                    sentTopics.add(topic1);
                }
                // Publish to the second topic if not already sent
                if (!sentTopics.contains(topic2)) {
                    client.publish(topic2, message);
                    System.out.println("Interpretation published to topic 2: " + topic2);
                    sentTopics.add(topic2);
                }
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
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
    public String processTransaction(String type, double amount, double oldbalanceOrg, double newbalanceOrig, double oldbalanceDest, double newbalanceDest, String nameOrig, String nameDest, String bankOrig, String bankDest) {
        TransactionDTO transactionDTO = createTransaction(type, amount, oldbalanceOrg, newbalanceOrig, oldbalanceDest, newbalanceDest, nameOrig, nameDest, bankOrig, bankDest);
        return sendTransactionToFlaskAPI(transactionDTO);
    }
//        publishInterpretation(transactionDTO);
    }
