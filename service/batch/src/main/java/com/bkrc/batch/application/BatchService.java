package com.bkrc.batch.application;

import com.bkrc.aladin.application.request.AladinRecommendRequest;
import com.bkrc.aladin.application.request.AladinRequest;
import com.bkrc.aladin.application.response.AladinBookResponse;
import com.bkrc.aladin.entity.AladinBook;
import com.bkrc.batch.client.AladinClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BatchService {

    private final AladinClient aladinClient;

    public void recommendScheduler() {
        List<AladinBook> aladinBookList = Collections.emptyList();
        List<AladinBook> successList = Collections.emptyList();
        int retry = 0;
        int maxRetry = 5;              // 최대 5회 재시도

        int page = 1;
        while (retry < maxRetry) {
            aladinBookList = aladinClient.getRecommendBooks(page);

            if (CollectionUtils.isEmpty(aladinBookList)) {
                retry ++;
                page ++;
                continue;
            } else {
                retry = 0;
            }
            successList = aladinClient.saveRecommendBook(aladinBookList);
        }
        if (!CollectionUtils.isEmpty(successList)) {
            String itemIds = successList.stream()
                    .map(book -> String.valueOf(book.getItemId()))
                    .collect(Collectors.joining(","));
            log.info("save success bookId : {}", itemIds);
        }
    }
}
