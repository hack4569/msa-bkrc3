package com.bkrc.aladin.application.request;

import com.bkrc.aladin.entity.AladinBook;

import java.util.List;

public record AladinRecommendRequest(List<AladinBook> newAladinBooks) {
}
