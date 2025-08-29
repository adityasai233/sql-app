package com.webhooksql.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookResponse {

    @JsonProperty("webhook")
    private String webhook;

    @JsonProperty("accessToken")
    private String accessToken;

    public WebhookResponse() {}

    public String getWebhook() { return webhook; }
    public void setWebhook(String webhook) { this.webhook = webhook; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    @Override
    public String toString() {
        return "WebhookResponse{" +
                "webhook='" + webhook + '\'' +
                ", accessToken='[PROTECTED]'" +
                '}';
    }
}
