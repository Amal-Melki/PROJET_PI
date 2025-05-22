package com.esprit.repositories;

import com.esprit.modules.Materiels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterielRepository extends JpaRepository<Materiels, Integer> {
}
