package com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import com.alura.screenmatch.model.DatosSerie;
import com.alura.screenmatch.model.DatosTemporadas;
import com.alura.screenmatch.service.ConsumoAPI;
import com.alura.screenmatch.service.ConvierteDatos;

public class Principal {
        private Scanner teclado = new Scanner(System.in);
        private ConsumoAPI consumoAPI = new ConsumoAPI();
        private final String URL_BASE = "http://www.omdbapi.com/?t=";
        private final String API_KEY = "&apikey=f876605f";
        private ConvierteDatos conversor = new ConvierteDatos();
        private List<DatosSerie> datosSeries = new ArrayList<>();

        public void muestraMenu() {
                var opcion = -1;
                while (opcion != 0) {
                        var menu = """
                                        1- Buscar series
                                        2- Buscar episodios
                                        3- Historial de búsqueda

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
                                case 0:
                                        System.out.println("Cerrando aplicación...");
                                        break;
                                default:
                                        System.out.println("Opcion inválida");
                        }
                }
        }

        private DatosSerie getDatosSerie() {
                System.out.println("Buscar serie: ");
                var nombreSerie = teclado.nextLine();
                var json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
                System.out.println(json);
                DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
                return datos;
        }

        private void buscarEpisodioPorSerie() {
                DatosSerie datosSerie = getDatosSerie();
                // Buscar datos de todas las temporadas
                List<DatosTemporadas> temporadas = new ArrayList<>();
                for (int i = 1; i < datosSerie.totalDeTemporadas(); i++) {
                        var json = consumoAPI
                                        .obtenerDatos(URL_BASE + datosSerie.titulo().replace(" ", "+") + "&Season=" + i
                                                        + API_KEY);
                        DatosTemporadas datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
                        temporadas.add(datosTemporadas);
                }
        }

        private void buscarSerieWeb() {
                DatosSerie datos = getDatosSerie();
                datosSeries.add(datos);
                System.out.println(datos);
        }

        private void historialDeBusqueda() {
                datosSeries.forEach(System.out::println);
        }
}
