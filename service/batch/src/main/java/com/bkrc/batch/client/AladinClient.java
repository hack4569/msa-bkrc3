package com.bkrc.batch.client;

import com.bkrc.aladin.application.request.AladinRecommendRequest;
import com.bkrc.aladin.application.request.AladinRecommendSaveRequest;
import com.bkrc.aladin.application.request.AladinRequest;
import com.bkrc.aladin.application.response.AladinBookPageResponse;
import com.bkrc.aladin.application.response.AladinBookResponse;
import com.bkrc.aladin.entity.AladinBook;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class AladinClient {
    private RestClient aladinServiceApi;
    @Value("${endpoints.aladin-service.url}")
    private String aladinServiceUrl;

    @PostConstruct
    public void initRestClient() {
        this.aladinServiceApi = RestClient.create(aladinServiceUrl);
    }

    public AladinBookPageResponse getAllBooks() {
        try {
            return aladinServiceApi.get()
                    .uri("/v1/aladin/books")
                    .retrieve()
                    .body(AladinBookPageResponse.class);
        } catch (Exception e) {
            log.error("AladinClient getAllBooks error", e.getMessage(), e);
            return new AladinBookPageResponse();
        }
    }

    public List<AladinBook> getRecommendBooks(int page) {
        try {
            return aladinServiceApi.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/aladin/books/recommend")
                            .queryParam("start", page)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AladinBook>>() {});
        } catch (Exception e) {
            log.error("AladinClient getRecommendBooks error", e.getMessage(), e);
            return List.of();
        }
    }

    public List<AladinBook> saveRecommendBook(List<AladinBook> newAladinBooks) {
        try {
            return aladinServiceApi.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/aladin/books/recommend")
                            .build())
                    .body(new AladinRecommendSaveRequest(newAladinBooks))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AladinBook>>() {});
        } catch (Exception e) {
            log.error("AladinClient saveRecommendBook error", e.getMessage(), e);
            return List.of();
        }
    }

    public List<AladinBook> getFilteredRecommendBooks(AladinRecommendRequest request) {
        try {
            return aladinServiceApi.post()
                    .uri("/v1/aladin/books/recommend/filter")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AladinBook>>() {});
        } catch (Exception e) {
            log.error("AladinClient getFilteredRecommendBooks error", e.getMessage(), e);
            return List.of();
        }
    }
}
