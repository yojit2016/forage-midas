package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KafkaConsumer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // 1. Validation Logic
        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {

            // 2. Call the Incentives API
            String url = "http://localhost:8080/incentive";
            Incentive incentiveResponse = restTemplate.postForObject(url, transaction, Incentive.class);
            float incentiveAmount = (incentiveResponse != null) ? incentiveResponse.getAmount() : 0f;

            // 3. Adjust Balances
            // Sender loses only the transaction amount
            sender.setBalance(sender.getBalance() - transaction.getAmount());

            // Recipient gets the transaction amount PLUS the incentive
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

            // 4. Save Updates to Database
            userRepository.save(sender);
            userRepository.save(recipient);

            // 5. Record the Transaction (including the incentive field)
            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
            transactionRecordRepository.save(record);

            // HELPER: This prints to your terminal so you can find the answer for Task 4
            UserRecord wilbur = userRepository.findByName("wilbur");
            if (wilbur != null) {
                System.out.println("WILBUR FINAL BALANCE: " + wilbur.getBalance());
            }
        }
    }
}