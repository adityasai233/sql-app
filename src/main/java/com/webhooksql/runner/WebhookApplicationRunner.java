package com.webhooksql.runner;

import com.webhooksql.model.WebhookResponse;
import com.webhooksql.model.WebhookRequest;
import com.webhooksql.service.WebhookService;
import com.webhooksql.service.SqlSolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class WebhookApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(WebhookApplicationRunner.class);

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private SqlSolverService sqlSolverService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("========================================");
        logger.info("Starting Webhook SQL Application Runner");
        logger.info("========================================");

        try {
            // Step 1: Generate webhook with registration details
            logger.info("Step 1: Generating webhook...");
            WebhookResponse webhookResponse = webhookService.generateWebhook();

            if (webhookResponse == null) {
                throw new RuntimeException("Failed to receive webhook response");
            }

            // Get the registration details from the POST request
            WebhookRequest requestDetails = webhookService.getCurrentRequest();
            String registrationNumber = webhookService.getRegistrationNumber();

            logger.info("Registration Details from POST request:");
            logger.info("Name: {}", requestDetails.getName());
            logger.info("Registration Number: {}", registrationNumber);
            logger.info("Email: {}", requestDetails.getEmail());

            String webhookUrl = webhookResponse.getWebhook();
            String accessToken = webhookResponse.getAccessToken();

            logger.info("Webhook generated successfully!");
            logger.info("Webhook URL: {}", webhookUrl);
            logger.info("Access Token received: {}", accessToken != null ? "[PRESENT]" : "[MISSING]");

            // Step 2: Solve SQL problem using the SAME registration number from POST request
            logger.info("Step 2: Solving SQL problem using registration number: {}", registrationNumber);
            String sqlSolution = sqlSolverService.solveSqlProblem(registrationNumber);

            if (sqlSolution == null || sqlSolution.trim().isEmpty()) {
                throw new RuntimeException("Failed to generate SQL solution");
            }

            logger.info("SQL problem solved successfully!");
            logger.info("Solution query length: {} characters", sqlSolution.length());

            // Store solution for audit purposes using the POST request registration number
            sqlSolverService.storeSolution(registrationNumber, sqlSolution);

            // Step 3: Submit solution to webhook using JWT token
            logger.info("Step 3: Submitting solution to webhook...");
            webhookService.submitSolution(webhookUrl, accessToken, sqlSolution);

            logger.info("========================================");
            logger.info("Application workflow completed successfully!");
            logger.info("Registration Number used: {}", registrationNumber);
            logger.info("Final SQL Query: {}", sqlSolution);
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("Application workflow failed!", e);
            logger.error("========================================");
            throw new RuntimeException("Application workflow failed", e);
        }
    }
}
