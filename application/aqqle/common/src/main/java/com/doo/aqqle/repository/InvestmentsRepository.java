package com.doo.aqqle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentsRepository extends JpaRepository<Investments, Long> {

    List<Investments> findAllByUseYn(String use);

}
