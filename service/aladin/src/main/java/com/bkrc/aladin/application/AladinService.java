package com.bkrc.aladin.application;

import com.bkrc.aladin.application.request.AladinRecommendRequest;
import com.bkrc.aladin.application.request.AladinRequest;
import com.bkrc.aladin.application.response.AladinBookResponse;
import com.bkrc.aladin.application.response.AladinResponse;
import com.bkrc.aladin.entity.*;
import com.bkrc.utils.LocalDateUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AladinService {
    private RestClient aladinApi;
    private final AladinBookRepository aladinBookRepository;
    private final BookCommentRepository bookCommentRepository;
    private final CategoryService categoryService;

    @Value("${aladin.host}")
    private String aladinHost;
    @Value("${aladin.ttbkey}")
    private String aladinTbKey;

    @PostConstruct
    void initRestClient() {
        aladinApi = RestClient.create(aladinHost);
    }

    public List<AladinBookResponse> getBooksForRecommend(AladinRequest aladinRequest) {
        var aladinBooks = this.getApi(AladinConstants.ITEM_LIST, aladinRequest).getItem();
        if (ObjectUtils.isEmpty(aladinBooks)) throw new AladinException("상품조회시 데이터가 없습니다.");

        return aladinBooks.stream().map(AladinBookResponse::from).toList();
    }

    public List<AladinBookResponse> findAll() {
        var aladinBooks = aladinBookRepository.findAll(Sort.by(Sort.Direction.DESC, "itemId"));
        aladinBooks.stream().forEach(i -> {
            var commentList = bookCommentRepository.findBookCommentsByAladinBookItemId(i.getItemId());
            if (Objects.nonNull(commentList)) i.setBookCommentList(commentList);
        });
        return aladinBooks.stream().map(AladinBookResponse::from).toList();
    }

    private AladinResponse getApi(String path, AladinRequest aladinRequest) {
        ResponseEntity<AladinResponse> response = null;
        try{
            response = aladinApi
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("ttbkey", aladinTbKey)
                            .queryParams(aladinRequest.getApiParamMap())
                            .build()
                    )
                    .retrieve()
                    .toEntity(AladinResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("[알라딘] 에러 메세지 파싱 에러 errorMessage={}", e.getMessage(), e);
            throw new AladinException("파싱에러");
        }

    }

    public List<AladinBookResponse> filteringForRecommend(AladinRecommendRequest aladinRecommendRequest) {
        var newAladinBooks = aladinRecommendRequest.newAladinBooks();
        if (ObjectUtils.isEmpty(aladinRecommendRequest.newAladinBooks())) return List.of();
        List<AladinBook> filtered = newAladinBooks.stream()
                .filter(categoryFilter())
                .filter(publicationDateFilter(this.anchorDate()))
                .toList();
        return filtered.stream().map(AladinBookResponse::from).toList();
    }

    private Predicate categoryFilter() {
        List<Category> categories = categoryService.findAcceptedCategories();
        var finalCids = categories.stream().map(Category::getCid).collect(Collectors.toCollection(HashSet::new));
        if (finalCids == null) return book -> true;
        // 허용된 카테고리만 필터링하는 Predicate
        Predicate<AladinBook> categoryFilter = book ->
                finalCids.contains(book.getCategoryId());
        return categoryFilter;
    }

    private String anchorDate() {
        //오늘 날짜
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormatter.format(cal.getTime());
        return today;
    }

    //출판일 필터링
    private Predicate publicationDateFilter(String publicationDate) {
        if (!StringUtils.hasText(publicationDate)) return book -> false;
        // 1년 이내 출간된 책을 필터링하는 Predicate
        Predicate<AladinBook> publicationDateFilter = book ->
                Integer.parseInt(publicationDate) >
                        Integer.parseInt(LocalDateUtils.getCustomDate(book.getPubDate()));
        return publicationDateFilter;
    }
}
