package com.alura.screenmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alura.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    
}
