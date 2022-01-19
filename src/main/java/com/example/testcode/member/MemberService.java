package com.example.testcode.member;

import com.example.testcode.domain.Member;
import com.example.testcode.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId);

    void validate(Long memberId);

    void notify(Study newstudy);

    void notify(Member member);
}
