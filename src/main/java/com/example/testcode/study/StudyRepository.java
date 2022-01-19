package com.example.testcode.study;

import com.example.testcode.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudyRepository extends JpaRepository<Study, Long> {
}