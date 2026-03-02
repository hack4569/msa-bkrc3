package com.bkrc.batch.application;

import com.bkrc.aladin.entity.AladinBook;
import com.bkrc.batch.client.AladinClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class BatchService {

    private final AladinClient aladinClient;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
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
                page ++;
                retry = 0;
            }
            successList.addAll(aladinClient.saveRecommendBook(aladinBookList));
        }
        if (!CollectionUtils.isEmpty(successList)) {
            String itemIds = successList.stream()
                    .map(book -> String.valueOf(book.getItemId()))
                    .collect(Collectors.joining(","));
            log.info("save success bookId : {}", itemIds);
        }
    }
}
