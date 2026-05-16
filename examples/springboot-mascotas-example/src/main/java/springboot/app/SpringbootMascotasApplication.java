package springboot.app;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springboot.app.utils.CargaDeDatos;

import java.io.IOException;

@SpringBootApplication
public class SpringbootMascotasApplication {

	@Autowired
	private CargaDeDatos cargaDeDatos;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMascotasApplication.class, args);

	}

	@PostConstruct
	public void init() throws IOException {
		cargaDeDatos.cargarDatosDesdeCSV();
	}



}
