package com.bkrc.member.dto;

import lombok.Data;

@Data
public class MemberDto {
    private long memberId;
    private String loginId;
    private String password;
}
