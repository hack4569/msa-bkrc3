package com.bkrc.member.application;

import com.bkrc.member.application.request.MemberRegisterRequest;
import com.bkrc.member.entity.Member;
import com.bkrc.member.entity.MemberException;
import com.bkrc.member.entity.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member saveMember(MemberRegisterRequest request) {
        checkDuplicateId(request);
        checkPwd(request);

        var member = Member.register(request, passwordEncoder);
        return memberRepository.save(member);
    }

    private void checkPwd(MemberRegisterRequest request) {
        if (!request.passwordCheck().equals(request.password())) {
            throw new MemberException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void checkDuplicateId(MemberRegisterRequest request) {
        if (memberRepository.findMemberByLoginId(request.loginId()).isPresent()) {
            throw new MemberException("이미 등록된 사용자 입니다.");
        }
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
