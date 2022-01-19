package com.example.testcode.study;

import com.example.testcode.domain.Member;
import com.example.testcode.domain.Study;
import com.example.testcode.domain.StudyStatus;
import com.example.testcode.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Test
    void createStudyNewService() {

        // 1. given
        StudyService studyService = new StudyService(memberService, studyRepository);

        Member member = new Member();
        member.setEmail("asdf@email.com");
        member.setId(1L);
        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        assertEquals("asdf@email.com", memberService.findById(1L).get().getEmail());

        Study study = new Study(10, "테스트");
        // * given 인데, when으로 조건을 주면 given/when/then의 BDD 방식과 어울리지 않으므로,
        // * when.thenReturn -> given.willReturn으로 바꿀 수 있다. (아래 두 줄은 동일하다.)
        // when(studyRepository.save(study)).thenReturn(study);  // 이전코드
        given(studyRepository.save(study)).willReturn(study);  // 새로운코드
        assertEquals("테스트", studyRepository.save(study).getName());

        // 2. when
        studyService.createNewStudy(1L, study);

        // 3. then
        assertNotNull(study.getOwnerId());
        assertEquals(member.getId(), study.getOwnerId());

        // * verify도 조금 더 BDD 스타일스럽게, then 을 사용해서 바꿀 수 있다.
        // verify(memberService, times(1)).notify(study);  // 이전코드
        // verify(memberService, times(1)).notify(member);  // 이전코드
        // verify(memberService, never()).validate(1L);  // 이전코드
        then(memberService).should(times(1)).notify(study);  // 새로운코드
        then(memberService).should(times(1)).notify(study);  // 새로운코드
        then(memberService).should(never()).validate(1L);  // 새로운코드

        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study);
        inOrder.verify(memberService).notify(member);

        // * 특정 시점 이후에 아무 일도 발생하지 않아야 한다면
        // verifyNoMoreInteractions(memberService);  // 이전코드
        // then(memberService).shouldHaveNoMoreInteractions();  // 새로운코드
    }

    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    @Test
    void openStudy() {
        // given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바 테스트");
        given(studyRepository.save(study)).willReturn(study);

        // when
        studyService.openStudy(study);

        // then
        assertEquals(StudyStatus.OPENED, study.getStatus());
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should().notify(study);
    }
}