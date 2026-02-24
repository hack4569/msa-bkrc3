package com.bkrc.aladin.application;

import com.bkrc.aladin.application.request.AladinRecommendRequest;
import com.bkrc.aladin.application.request.AladinRequest;
import com.bkrc.aladin.application.response.AladinBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AladinController {
    private final AladinService aladinService;

    @GetMapping("/v1/aladin/recommend/books")
    public List<AladinBookResponse> getRecommendbooks(AladinRequest aladinRequest) {
        return aladinService.bookListForBatch(aladinRequest);
    }

    @GetMapping("/v1/aladin/books")
    public List<AladinBookResponse> getAllBooks() {
        return aladinService.findAll();
    }

    @GetMapping("/v1/aladin/recommend/books")
    public List<AladinBookResponse> getRecommendBooks(AladinRecommendRequest aladinRecommendRequest) {
        return aladinService.filteringForRecommend(aladinRecommendRequest);
    }
}
