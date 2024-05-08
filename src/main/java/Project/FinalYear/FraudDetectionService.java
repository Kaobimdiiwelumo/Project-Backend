//package Project.FinalYear;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FraudDetectionService {
//    @Autowired
//    private MqttConfig.MyMqttGateway mqttGateway;
//
//    public void detectAndPublishFraud(String transactionData) {
//        // Your fraud detection logic here
//
//        // If fraud is detected, publish an alert to MQTT
//        if (isFraudDetected(transactionData)) {
//            mqttGateway.sendToMqtt("Fraud Alert: " + transactionData);
//        }
//    }
//
//    private boolean isFraudDetected(String transactionData) {
//        // Implement your fraud detection logic
//        // For example, you might check transaction patterns, amounts, etc.
//        // Return true if fraud is detected, false otherwise
//        return false;
//    }
//}
