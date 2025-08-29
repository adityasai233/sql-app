package com.webhooksql.service;

import com.webhooksql.model.WebhookRequest;
import com.webhooksql.model.WebhookResponse;
import com.webhooksql.model.SolutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private static final String WEBHOOK_GENERATION_URL =
        "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    private static final String WEBHOOK_SUBMISSION_URL =
        "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    @Autowired
    private RestTemplate restTemplate;

    // Store the registration request details
    private WebhookRequest currentRequest;

    /**
     * Generates webhook by making POST request with registration details
     * Returns both webhook response and stores the request for later use
     */
    public WebhookResponse generateWebhook() {
        try {
            logger.info("Initiating webhook generation process...");

            // Create request with actual registration details
            this.currentRequest = new WebhookRequest(
                "Aditya",
                "22BCE0721",
                "aditya@gmail.com"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("User-Agent", "SpringBoot-WebhookSQL-Client/1.0");

            HttpEntity<WebhookRequest> entity = new HttpEntity<>(currentRequest, headers);

            logger.info("Sending webhook generation request: {}", currentRequest);

            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                WEBHOOK_GENERATION_URL,
                entity,
                WebhookResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Webhook generated successfully for regNo: {}", currentRequest.getRegNo());
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to generate webhook: " + response.getStatusCode());
            }

        } catch (RestClientException e) {
            logger.error("Error generating webhook", e);
            throw new RuntimeException("Failed to generate webhook: " + e.getMessage(), e);
        }
    }

    /**
     * Get the registration number from the POST request
     */
    public String getRegistrationNumber() {
        return currentRequest != null ? currentRequest.getRegNo() : null;
    }

    /**
     * Get the complete registration request details
     */
    public WebhookRequest getCurrentRequest() {
        return currentRequest;
    }

    /**
     * Submits the SQL solution to the webhook URL using JWT authentication
     */
    public void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            logger.info("Submitting solution to webhook: {}", webhookUrl);
            logger.info("Using registration number: {}", getRegistrationNumber());

            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken); // JWT token as per requirement
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

            logger.info("Solution request: {}", solutionRequest);

            // Submit to the specific webhook URL from response
            String submissionUrl = webhookUrl != null ? webhookUrl : WEBHOOK_SUBMISSION_URL;

            ResponseEntity<String> response = restTemplate.postForEntity(
                submissionUrl,
                entity,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Solution submitted successfully. Response: {}", response.getBody());
            } else {
                logger.warn("Solution submission returned status: {}", response.getStatusCode());
            }

        } catch (RestClientException e) {
            logger.error("Error submitting solution", e);
            throw new RuntimeException("Failed to submit solution: " + e.getMessage(), e);
        }
    }
}
