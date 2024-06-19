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
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
