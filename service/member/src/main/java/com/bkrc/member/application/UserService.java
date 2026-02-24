package com.bkrc.member.application;

import com.bkrc.member.application.request.MemberRegisterRequest;
import com.bkrc.member.entity.Member;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    Member saveMember(MemberRegisterRequest request);
    Member getMemberByMemberId(String memberId);
    List<Member> getAllMembers();
}
