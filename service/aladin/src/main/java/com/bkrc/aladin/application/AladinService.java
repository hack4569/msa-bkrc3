package com.bkrc.aladin.application;

import com.bkrc.aladin.application.request.AladinRecommendRequest;
import com.bkrc.aladin.application.request.AladinRecommendSaveRequest;
import com.bkrc.aladin.application.request.AladinRequest;
import com.bkrc.aladin.application.response.AladinBookPageResponse;
import com.bkrc.aladin.application.response.AladinBookResponse;
import com.bkrc.aladin.application.response.AladinResponse;
import com.bkrc.aladin.entity.*;
import com.bkrc.utils.LocalDateUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AladinService {

    private static final String CACHE_KEY_ALL_BOOKS = "aladin:books:all";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private RestClient aladinApi;
    private final AladinBookRepository aladinBookRepository;
    private final BookCommentRepository bookCommentRepository;
    private final CategoryService categoryService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aladin.host}")
    private String aladinHost;
    @Value("${aladin.ttbkey}")
    private String aladinTbKey;

    @PostConstruct
    void initRestClient() {
        aladinApi = RestClient.create(aladinHost);
    }

    public List<AladinBook> getBooksForRecommend(AladinRequest aladinRequest) {
        var allBooksResponse = this.findAll();
        var registeredBooks = allBooksResponse.getAladinBookResponseList();
        Set<Integer> registeredBookItemIds;
        if (!CollectionUtils.isEmpty(registeredBooks)) {
            registeredBookItemIds = registeredBooks.stream().map(AladinBookResponse::getItemId).collect(Collectors.toSet());
        } else {
            registeredBookItemIds = new HashSet<>();
        }
        var aladinBooks = this.getApi(AladinConstants.ITEM_LIST, aladinRequest).getItem();
        if (ObjectUtils.isEmpty(aladinBooks)) throw new AladinException("상품조회시 데이터가 없습니다.");
        var newAladinBooks = aladinBooks.stream().filter(i -> !registeredBookItemIds.contains(i.getItemId())).toList();
        List<AladinBook> filtered = newAladinBooks.stream()
                .filter(categoryFilter())
                .filter(publicationDateFilter(this.anchorDate()))
                .toList();
        return filtered;

    }

    public AladinBookPageResponse findAll() {
        try {
            String cached = redisTemplate.opsForValue().get(CACHE_KEY_ALL_BOOKS);
            if (StringUtils.hasText(cached)) {
                return objectMapper.readValue(cached, AladinBookPageResponse.class);
            }
        } catch (Exception e) {
            log.warn("[알라딘] 캐시 조회 실패, DB에서 조회합니다. key={}", CACHE_KEY_ALL_BOOKS, e);
        }
        var response = findAllFromDb();
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(CACHE_KEY_ALL_BOOKS, json, CACHE_TTL);
        } catch (Exception e) {
            log.warn("[알라딘] 캐시 저장 실패. key={}", CACHE_KEY_ALL_BOOKS, e);
        }
        return response;
    }

    private AladinBookPageResponse findAllFromDb() {
        var aladinBooks = aladinBookRepository.findAllWithBookComments();
        return AladinBookPageResponse.of(aladinBooks.stream().map(AladinBookResponse::from).toList(), aladinBooks.size());
    }

    private void evictAllBooksCache() {
        try {
            redisTemplate.delete(CACHE_KEY_ALL_BOOKS);
            log.debug("[알라딘] 전체 책 목록 캐시 삭제. key={}", CACHE_KEY_ALL_BOOKS);
        } catch (Exception e) {
            log.warn("[알라딘] 캐시 삭제 실패. key={}", CACHE_KEY_ALL_BOOKS, e);
        }
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

    public List<AladinBook> saveNewAladinBooks(AladinRecommendSaveRequest request) {
        var aladinBooks = request.newAladinBooks();
        if (!CollectionUtils.isEmpty(aladinBooks)) {
            List<AladinBook> aladinDetailList = new ArrayList<>();
            aladinBooks.forEach( aladinBook -> {
                var aladinDetail = this.bookDetail(AladinRequest.create(aladinBook.getIsbn13()));
                aladinDetailList.add(aladinDetail);
            });
            List<AladinBook> saved = aladinBookRepository.saveAll(aladinDetailList);
            //evictAllBooksCache();
            return saved;
        }
        return List.of();
    }

    //책 상세 조회
    public AladinBook bookDetail(AladinRequest aladinRequest) {
        var aladinBooks = this.getApi(AladinConstants.ITEM_LOOKUP, aladinRequest).getItem();
        if (aladinBooks.isEmpty()) throw new AladinException("상품조회시 데이터가 없습니다.");

        var aladinbook = aladinBooks.get(0);
        //코멘트 세팅
        aladinbook.settingBookCommentList();
        return aladinbook;
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
