package com.bkrc.aladin.application;

import com.bkrc.aladin.application.request.AladinRecommendRequest;
import com.bkrc.aladin.application.request.AladinRecommendSaveRequest;
import com.bkrc.aladin.application.request.AladinRequest;
import com.bkrc.aladin.application.response.AladinBookPageResponse;
import com.bkrc.aladin.application.response.AladinBookResponse;
import com.bkrc.aladin.entity.AladinBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AladinController {
    private final AladinService aladinService;

    @GetMapping("/v1/aladin/books")
    public AladinBookPageResponse getAllBooks() {
        return aladinService.findAll();
    }

    @GetMapping("/v1/aladin/books/recommend")
    public List<AladinBook> getRecommendbooks(AladinRequest aladinRequest) {
        return aladinService.getBooksForRecommend(aladinRequest);
    }

//    @GetMapping("/v1/aladin/books/recommend/filter")
//    public List<AladinBook> getFilteredRecommendBooks(AladinRecommendRequest aladinRecommendRequest) {
//        return aladinService.filteringForRecommend(aladinRecommendRequest);
//    }

    @PostMapping("/v1/aladin/books/recommend")
    public List<AladinBook> addRecommendBook(@RequestBody AladinRecommendSaveRequest request) {
        return aladinService.saveNewAladinBooks(request);
    }
}
