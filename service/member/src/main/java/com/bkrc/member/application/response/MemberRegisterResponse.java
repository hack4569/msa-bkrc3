package com.bkrc.member.application.response;

import com.bkrc.member.entity.Member;

public record MemberRegisterResponse(String loginId) {
    public static MemberRegisterResponse of(Member member) {
        return new MemberRegisterResponse(member.getLoginId());
    }
}
