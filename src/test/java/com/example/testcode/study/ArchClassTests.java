package com.example.testcode.study;

import com.example.testcode.App;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = App.class)
public class ArchClassTests {

    /**
     * 1. StudyController는 StudyService와 StudyRepository를 사용할 수 있다.
     * 2. StudyRepository는 StudyService와 StudyController를 사용할 수 없다.
     * 3. Study 로 시작하는 클래스들은 Study 패키지 내에 있어야 한다.
     * (단, Study는 Entity이고 StudyStatus는 Enum이기 때문에 domain에 해당하므로 제외시켜준다.)
     */

    // 1번 테스트
    @ArchTest
    ArchRule controllerClassRule = classes().that().haveSimpleNameEndingWith("Controller")
            .should().accessClassesThat().haveSimpleNameEndingWith("Service")
            .orShould().accessClassesThat().haveSimpleNameEndingWith("Repository");

    // 2번 테스트
    @ArchTest
    ArchRule repositoryClassRule = noClasses().that().haveSimpleNameEndingWith("Repository")
            .should().accessClassesThat().haveSimpleNameEndingWith("Service")
            .orShould().accessClassesThat().haveSimpleNameEndingWith("Controller");

    // 3번 테스트
    @ArchTest
    ArchRule studyClassNameRule = classes().that().haveSimpleNameStartingWith("Study")
            .and().areNotEnums()
            .and().areAnnotatedWith("Entity")
            .should().resideInAPackage("..study..");
}
