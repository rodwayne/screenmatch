package com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.alura.screenmatch.model.DatosEpisodio;
import com.alura.screenmatch.model.DatosSerie;
import com.alura.screenmatch.model.DatosTemporadas;
import com.alura.screenmatch.model.Episodio;
import com.alura.screenmatch.service.ConsumoAPI;
import com.alura.screenmatch.service.ConvierteDatos;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f876605f";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraMenu() {
        System.out.println("Buscar serie: ");
        // Buscar datos generales de la serie
        var nombreSerie = teclado.nextLine();
        var json = consumoAPI
                .obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);

        // Buscar datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i < datos.totalDeTemporadas(); i++) {
            json = consumoAPI
                    .obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }

        // temporadas.forEach(System.out::println);

        // Show each episode title for a season
        // temporadas.forEach(t -> t.episodios().forEach(e ->
        // System.out.println(e.titulo())));

        // Convert data to List<EpisodeData>
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // Top 5 episodes
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primer filtro (N/A)" + e))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion)
                        .reversed())
                .peek(e -> System.out.println("Segunda ordenación (M > m)" + e))
                .map(e -> e.titulo().toUpperCase())
                .limit(5)
                .forEach(System.out::println);

        // Convert data to a Episode type list
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        // episodios.forEach(System.out::println);

        // Busqueda de episodios a partir de cierta fecha
        // System.out.println("Buscar año de los episodios:");
        // var fecha = teclado.nextInt();
        // teclado.nextLine();

        // LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // episodios.stream()
        //         .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
        //         .forEach(e -> System.out.println(
        //             "Temporada " + e.getTemporada() +
        //             " Episodio " +e.getTitulo() +
        //             "Fecha de lanzamiento " + e.getFechaDeLanzamiento().format(dtf)
        //         ));

        // Look for episodes by trimmed title
        System.out.println("Buscar episodio:");
        var pedazoTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
                .findFirst();

        if (episodioBuscado.isPresent()) {
                System.out.println("Episodio encontrado");
                System.out.println("Los datos son: " + episodioBuscado.get());
        } else {
                System.out.println("Episodio no encontrado");
        }
    }
}
