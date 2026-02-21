package com.bkrc.aladin.application.response;

import lombok.Getter;

import java.util.List;

@Getter
public class AladinBookPageResponse {

    private List<AladinBookResponse> aladinBookResponseList;
    private Long count;

    public static AladinBookPageResponse of(final List<AladinBookResponse> aladinBookResponseList, final Long count) {
        AladinBookPageResponse aladinBookPageResponse = new AladinBookPageResponse();
        aladinBookPageResponse.aladinBookResponseList = aladinBookResponseList;
        aladinBookPageResponse.count = count;
        return aladinBookPageResponse;
    }
}
