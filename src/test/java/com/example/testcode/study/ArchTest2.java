package com.example.testcode.study;

import com.example.testcode.App;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

//패키지를 설정해주는 방법은 아래 두 가지가 있다.
//@AnalyzeClasses(packages = "com.example.testcode")
@AnalyzeClasses(packagesOf = App.class)
public class ArchTest2 {

    /**
     * 1. domain 패키지의 클래스들은 member, study, domain 패키지에서만 참조 가능하다.
     * 2. member 패키지의 클래스들은 member, study 패키지에서만 참조 가능하다.
     * 3. domain 패키지는 member 패키지를 참조할 수 없다.
     * 4. study 패키지는 study 패키지에서만 참조할 수 있다.
     * 5. 순환 참조가 없어야 한다.
     */

    // 1번 테스트
    @ArchTest
    ArchRule domainPageRule = classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("..domain..", "..member..", "..study..");

    // 2번 테스트
    @ArchTest
    ArchRule memberPackageRule = classes().that().resideInAPackage("..member..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("..member..", "..study..");

    // 3번 테스트
    @ArchTest
    ArchRule memberPackageRule2 = noClasses().that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAPackage("..member..");

    // 4번 테스트
    @ArchTest
    ArchRule studypackageRule = noClasses().that().resideOutsideOfPackage("..study..")
            .should().accessClassesThat().resideInAPackage("..study..");

    // 5번 테스트
    @ArchTest
    ArchRule freeOfCycles = slices().matching("..testcode.(*)..")
            .should().beFreeOfCycles();


}
