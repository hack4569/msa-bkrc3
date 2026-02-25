package com.bkrc.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BatchService {

    private RestClient aladinApi;
    @Value("${endpoints.aladin-service.url}")
    private String aladinHost;

    @PostConstruct
    void initRestClient() {
        aladinApi = RestClient.create(aladinHost);
    }
    public void recommendScheduler() {

    }
}
