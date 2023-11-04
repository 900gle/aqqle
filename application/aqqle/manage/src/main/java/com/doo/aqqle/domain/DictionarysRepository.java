package com.doo.aqqle.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DictionarysRepository extends JpaRepository<Dictionarys, Long> {

    List<Dictionarys> findAllByUseYn(String use);
    List<Dictionarys> findAllByWord(String Keyword);
    Dictionarys findAllById(Long id);
}
