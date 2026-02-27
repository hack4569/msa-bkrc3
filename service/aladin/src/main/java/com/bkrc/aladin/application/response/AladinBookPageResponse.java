package com.bkrc.aladin.application.response;

import lombok.Getter;

import java.util.List;

@Getter
public class AladinBookPageResponse {

    private List<AladinBookResponse> aladinBookResponseList;
    private int count;

    public static AladinBookPageResponse of(final List<AladinBookResponse> aladinBookResponseList, final int count) {
        AladinBookPageResponse aladinBookPageResponse = new AladinBookPageResponse();
        aladinBookPageResponse.aladinBookResponseList = aladinBookResponseList;
        aladinBookPageResponse.count = count;
        return aladinBookPageResponse;
    }
}
