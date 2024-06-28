package com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.alura.screenmatch.model.Categoria;
import com.alura.screenmatch.model.DatosSerie;
import com.alura.screenmatch.model.DatosTemporadas;
import com.alura.screenmatch.model.Episodio;
import com.alura.screenmatch.model.Serie;
import com.alura.screenmatch.repository.SerieRepository;
import com.alura.screenmatch.service.ConsumoAPI;
import com.alura.screenmatch.service.ConvierteDatos;

public class Principal {
        private Scanner teclado = new Scanner(System.in);
        private ConsumoAPI consumoAPI = new ConsumoAPI();
        private final String URL_BASE = "http://www.omdbapi.com/?t=";
        private final String API_KEY = System.getenv("API_KEY");
        private String apiUrl = "&apikey=" + API_KEY;
        private ConvierteDatos conversor = new ConvierteDatos();
        private SerieRepository repositorio;
        private List<Serie> series;

        public Principal(SerieRepository repository) {
                this.repositorio = repository;
                checkApi();
        }

        public void muestraMenu() {
                var opcion = -1;
                while (opcion != 0) {
                        var menu = """
                                        1- Buscar series
                                        2- Buscar episodios
                                        3- Historial de búsqueda
                                        4- Buscar serie por titulo
                                        5- Top 5 mejores series
                                        6- Buscar series por categoria
                                        7- Filtrar series por temporada y evaluación
                                        8- Buscar episodio por titulo

                                        0- Salir
                                        """;
                        System.out.println(menu);
                        opcion = teclado.nextInt();
                        teclado.nextLine();

                        switch (opcion) {
                                case 1:
                                        buscarSerieWeb();
                                        break;
                                case 2:
                                        buscarEpisodioPorSerie();
                                        break;
                                case 3:
                                        historialDeBusqueda();
                                        break;
                                case 4:
                                        buscarSeriePorTitulo();
                                        break;
                                case 5:
                                        buscarTop5Series();
                                        break;
                                case 6:
                                        buscarSeriesPorCategoria();
                                        break;
                                case 7:
                                        filtrarSeriesPorTemporadaYEvaluacion();
                                        break;
                                case 8:
                                        buscarEpisodioPorTitulo();
                                        break;
                                case 0:
                                        System.out.println("Cerrando aplicación...");
                                        break;
                                default:
                                        System.out.println("Opcion inválida");
                        }
                }
        }

        private void checkApi() {
                // System.out.println(API_KEY);
                if (API_KEY == null) {
                        throw new IllegalStateException("Variable de entorno API_KEY no definida");
                }
        }

        private DatosSerie getDatosSerie() {
                System.out.println("Buscar serie: ");
                var nombreSerie = teclado.nextLine();
                var json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + apiUrl);
                // System.out.println(json);
                DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
                return datos;
        }

        private void buscarEpisodioPorSerie() {
                historialDeBusqueda();
                System.out.println("Escribe la serie para buscar episodios:");
                var nombreSerie = teclado.nextLine();

                Optional<Serie> serie = series.stream()
                                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                                .findFirst();

                if (serie.isPresent()) {
                        var serieEncontrada = serie.get();
                        List<DatosTemporadas> temporadas = new ArrayList<>();
                        for (int i = 1; i < serieEncontrada.getTotalDeTemporadas(); i++) {
                                var json = consumoAPI
                                                .obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+")
                                                                + "&Season=" + i
                                                                + apiUrl);
                                DatosTemporadas datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
                                temporadas.add(datosTemporadas);
                        }
                        // temporadas.forEach(System.out::println);
                        List<Episodio> episodios = temporadas.stream()
                                        .flatMap(d -> d.episodios().stream()
                                                        .map(e -> new Episodio(d.numero(), e)))
                                        .collect(Collectors.toList());

                        serieEncontrada.setEpisodios(episodios);
                        repositorio.save(serieEncontrada);
                }
        }

        private void buscarSerieWeb() {
                DatosSerie datos = getDatosSerie();
                // datosSeries.add(datos);
                Serie serie = new Serie(datos);
                repositorio.save(serie);
                System.out.println(datos);
        }

        private void historialDeBusqueda() {
                series = repositorio.findAll();

                series.stream()
                                .sorted(Comparator.comparing(Serie::getGenero))
                                .forEach(System.out::println);
        }

        private void buscarSeriePorTitulo() {
                System.out.println("Escribe la serie que quieres buscar: ");
                var nombreSerie = teclado.nextLine();
                Optional<Serie> serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

                if (serieBuscada.isPresent()) {
                        System.out.println("la serie buscada es: " + serieBuscada.get());
                } else {
                        System.out.println("Serie no encontrada");
                }
        }

        private void buscarTop5Series() {
                List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
                topSeries.forEach(s -> System.out
                                .println("Serie: " + s.getTitulo() + " Evaluacion: " + s.getEvaluacion()));
        }

        private void buscarSeriesPorCategoria() {
                System.out.println("Escriba el genero/categoria: ");
                var genero = teclado.nextLine();
                var categoria = Categoria.fromEspanol(genero);
                List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
                System.out.println("Las series de la categoría " + genero);
                seriesPorCategoria.forEach(System.out::println);
        }

        private void filtrarSeriesPorTemporadaYEvaluacion() {
                System.out.println("Número de temporadas de las series:");
                var totalTemporadas = teclado.nextInt();
                teclado.nextLine();
                System.out.println("Evaluación de las series");
                var evaluacion = teclado.nextDouble();
                teclado.nextLine();
                List<Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion);
                System.out.println("Series encontradas con máximo " + totalTemporadas + " temporadas y evaluación de "
                                + evaluacion);
                filtroSeries.forEach(s -> System.out.println(s.getTitulo() + " - Evaluación: " + s.getEvaluacion()));
        }

        private void buscarEpisodioPorTitulo() {
                System.out.println("Nombre del episodio:");
                var nombreEpisodio = teclado.nextLine();
                List<Episodio> episodiosEncontrados = repositorio.episodioPorNombre(nombreEpisodio);
                episodiosEncontrados.forEach(
                                e -> System.out.printf("Serie: %s Temporada %s Episodio %s Evaluacion %s\n",
                                                e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
        }
}
