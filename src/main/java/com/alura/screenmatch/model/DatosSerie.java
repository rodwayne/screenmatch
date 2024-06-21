package com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(
    // Add: Genero, Sinopsis, Poster, Actores
    @JsonAlias("Title") String titulo,
    @JsonAlias("Genre") String genero,
    @JsonAlias("Actors") String actores,
    @JsonAlias("Plot") String sinopsis,
    @JsonAlias("Poster") String poster,
    @JsonAlias("totalSeasons") Integer totalDeTemporadas,
    @JsonAlias("imdbRating") String evaluacion
) {

}
