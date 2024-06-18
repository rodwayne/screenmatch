package com.alura.screenmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alura.screenmatch.model.DatosEpisodio;
import com.alura.screenmatch.model.DatosSerie;
import com.alura.screenmatch.service.ConsumoAPI;
import com.alura.screenmatch.service.ConvierteDatos;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoAPI = new ConsumoAPI();
		ConvierteDatos conversor = new ConvierteDatos();
		var json = consumoAPI
				.obtenerDatos("http://www.omdbapi.com/?t=game+of+thrones&Season=1&episode=1&apikey=f876605f");
		DatosEpisodio episodios = conversor.obtenerDatos(json, DatosEpisodio.class);
		System.out.println(episodios);
	}

}
