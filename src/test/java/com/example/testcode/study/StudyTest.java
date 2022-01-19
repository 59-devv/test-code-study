package com.example.testcode.study;

import com.example.testcode.domain.Study;
import com.example.testcode.domain.StudyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class StudyTest {

    // 테스트를 랜덤값으로 여러번 실행한다거나 할 때 여러변 반복하게 설정할 수 있다.
    @DisplayName("스터디 반복")
    @RepeatedTest(value=10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void create_new_study_repeat() {
        Study study = new Study(10);
        assertTrue(study.getLimitCount() > 0, "Study의 참석자는 0명보다 많아야 한다.");
        assertNotNull(study);
        assertEquals(StudyStatus.DRAFT, study.getStatus(), "Study를 처음 만들면 값이 DRAFT여야 한다.");
    }

    // 매개변수들을 지정해서, 반복 테스트를 수행할 수 있다.
    @DisplayName("매개변수 세팅 테스트")
    @ParameterizedTest(name = "{index}.{displayName}-{0}")
    @ValueSource(strings = {"동해물과", "백두산이", "마르고", "닳도록"})
    void create_new_study_paramiterized(String message) {
        System.out.println(message);
    }

    //@ParameterizedTest를 사용할 경우, 매개변수들을 지정할 수 있는 다양한 어노테이션이 있다.
    //@EmptySource : 빈 값을 매개변수에 포함
    //@NullSource : null 값을 매개변수에 포함
    //@NullAndEmptySource : 빈 값과 null 값을 모두 매개변수에 포함

    // ----------------------------------------------------------
    // 특정 클래스 타입으로 매개변수를 받고, 인자들을 넣어서 테스트 할 수도 있다.
    // SimpleArgumentConverter를 통해서 변환 후 사용해야 한다.

    // 1. 변환
    // 하나의 인자만 받을 경우, SimpleArgumentConverter로 변환한다.
    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study Class");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    // 2. 사용 (생성자 하나만 받기)
    // 만들어 둔 converter를 지정해주기 위해, @ConvertWith 어노테이션을 사용한다.
    @DisplayName("클래스를 매개변수로 세팅 테스트")
    @ParameterizedTest(name = "{index}-{0}")
    @ValueSource(ints = {10, 20, 40})
    void create_new_study_paramiterized_class(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimitCount());
    }

    // 3-1. 사용 (생성자 두개 받기 / 직접 받기)
    // 두 개의 인자를 모두 받기 위해 CsvSource를 사용해야 한다.
    // Csv 값 안에 공백을 넣고싶을 경우, 다시 ''로 감싼다.
    @DisplayName("클래스를 매개변수로 세팅 테스트")
    @ParameterizedTest(name = "(인자2개) {index}-{0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void create_new_study_paramiterized_class2(int limit, String name) {
        Study study = new Study(limit, name);
        System.out.println(study);
    }

    // 3-2. 사용 (생성자 두개 받기 / 변환하기)
    // 두 개의 인자를 모두 받기 위해 ArgumentsAccessor를 사용한다.
    @DisplayName("클래스를 매개변수로 세팅 테스트")
    @ParameterizedTest(name = "(Accessor) {index}-{0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void create_new_study_paramiterized_class3(ArgumentsAccessor accessor) {
        Study study = new Study(accessor.getInteger(0), accessor.getString(1));
        System.out.println(study);
    }

    // 3-3. 사용 (생성자 두개 받기 / 변환하기2)
    // 두 개의 인자를 모두 받기 위해 ArgumentsAggregator를 생성해준다.
    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

    @DisplayName("클래스를 매개변수로 세팅 테스트")
    @ParameterizedTest(name = "(Aggregator) {index}-{0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void create_new_study_paramiterized_class4(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }
}