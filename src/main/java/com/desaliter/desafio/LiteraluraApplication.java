package com.desaliter.desafio;

import com.desaliter.desafio.principal.Principal;
import com.desaliter.desafio.repository.AutorRepository;
import com.desaliter.desafio.repository.LibroRepository;
import com.desaliter.desafio.service.AutorService;
import com.desaliter.desafio.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	private final Principal principal;

    @Autowired
	private LibroRepository libroRepository;

    @Autowired
	private AutorRepository autorRepository;

    @Autowired
	private LibroService libroService;

	@Autowired
	private AutorService autorService;

    public LiteraluraApplication(Principal principal) {
        this.principal = principal;
    }

    public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override

	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroService, autorService,libroRepository , autorRepository);
     principal.mostrarMenu();
	}
}

