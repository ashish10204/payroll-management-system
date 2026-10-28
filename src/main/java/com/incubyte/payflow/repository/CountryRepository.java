package com.incubyte.payflow.repository;

import com.incubyte.payflow.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}
