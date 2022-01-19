package com.example.testcode.study;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchTest {

    @Test
    public void packageDependencyTests() {

        JavaClasses classes = new ClassFileImporter().importPackages("com.example.testcode");

        /**
         * 1. domain 패키지의 클래스들은 member, study, domain 패키지에서만 참조 가능하다.
         * 2. member 패키지의 클래스들은 member, study 패키지에서만 참조 가능하다.
         * 3. domain 패키지는 member 패키지를 참조할 수 없다.
         * 4. study 패키지는 study 패키지에서만 참조할 수 있다.
         * 5. 순환 참조가 없어야 한다.
         */

        // 1번 테스트
        ArchRule domainPageRule = classes().that().resideInAPackage("..domain..")
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage("..domain..", "..member..", "..study..");

        domainPageRule.check(classes);

        // 2번 테스트
        ArchRule memberPackageRule = classes().that().resideInAPackage("..member..")
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage("..member..", "..study..");

        domainPageRule.check(classes);


        // 3번 테스트
        ArchRule memberPackageRule2 = noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAPackage("..member..");

        memberPackageRule2.check(classes);


        // 4번 테스트
        ArchRule studypackageRule = noClasses().that().resideOutsideOfPackage("..study..")
                .should().accessClassesThat().resideInAPackage("..study..");

        studypackageRule.check(classes);

        // 5번 테스트
        ArchRule freeOfCycles = slices().matching("..testcode.(*)..")
                .should().beFreeOfCycles();

        freeOfCycles.check(classes);
    }
}
