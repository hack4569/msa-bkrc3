package com.bkrc.aladin.application;

import com.bkrc.aladin.application.response.AladinBookPageResponse;
import com.bkrc.aladin.application.response.AladinBookResponse;
import com.bkrc.aladin.entity.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AladinService {
    private RestClient aladinApi;
    private AladinBookRepository aladinBookRepository;
    private BookCommentRepository bookCommentRepository;
    @Value("${aladin.host}")
    private String aladinHost;
    @Value("${aladin.ttbkey}")
    private String aladinTbKey;

    @PostConstruct
    void initRestClient() {
        aladinApi = RestClient.create(aladinHost);
    }

    public List<AladinBookResponse> bookListForBatch(AladinRequest aladinRequest) {
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
}
