package com.bkrc.member.application;

import com.bkrc.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final MemberRepository memberRepository;

    @Override
    public Member saveMember(Member member) {
        return null;
    }

    @Override
    public Member getMemberByMemberId(String memberId) {
        return null;
    }

    @Override
    public List<Member> getAllMembers() {
        return List.of();
    }

//    public Member login(String loginId){
//        return memberRepository.findMemberByLoginId(loginId).orElse(null);
//    }

    //@Transactional
//    public void saveMember(Member member) {
//        memberRepository.save(member);
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
