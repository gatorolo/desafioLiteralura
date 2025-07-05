package com.desaliter.desafio.principal;

import com.desaliter.desafio.dto.AutorDTO;
import com.desaliter.desafio.dto.DatosRespuestaDTO;
import com.desaliter.desafio.dto.LibroDTO;
import com.desaliter.desafio.model.Autor;
import com.desaliter.desafio.model.Libro;
import com.desaliter.desafio.repository.AutorRepository;
import com.desaliter.desafio.repository.LibroRepository;
import com.desaliter.desafio.service.AutorService;
import com.desaliter.desafio.service.ConsumoApi;
import com.desaliter.desafio.service.ConvierteDatos;
import com.desaliter.desafio.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {


    private ConsumoApi consumoApi = new ConsumoApi();

    @Autowired
    private LibroService libroService;

    private ConvierteDatos conversor = new ConvierteDatos();

    @Autowired
    private AutorService autorService;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    private Scanner teclado = new Scanner(System.in);

    private static final String BASE_URL = "https://gutendex.com/books/";

    public Principal(LibroService libroService, AutorService autorService,
                     LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroService = libroService;
        this.autorService = autorService;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        int option;

        do {
            System.out.println("    --------------------------------------------------------");
            System.out.println("***************Ingresa la opción que deseas***************\n");
            System.out.println("--------------->1. Buscar Libro por Titulo");
            System.out.println("--------------->2. Listar por Libros");
            System.out.println("--------------->3. Buscar Por Autores");
            System.out.println("--------------->4. Buscar Autores vivos en Determinado Año");
            System.out.println("--------------->5. Buscar Libro por Idioma");
            System.out.println("--------------->9. Salir");

            try {
                option = teclado.nextInt();
                teclado.nextLine();
                switch (option) {
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        listarLibros();
                        break;
                    case 3:
                        buscarAutorPorNombre();
                        break;
                    case 4:
                        System.out.println("falta");
                        break;
                    case 5:
                        System.out.println("falta");
                        break;
                    case 9:
                        System.out.println("Gracias por utilizar Gutendex-Alura-On-line");
                        break;
                    default:
                        System.out.println("Opción inválida❌");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingresa un número para la opción del menú❌");
                teclado.nextLine();
                option = -1;
            }
        } while (option != 9);
        teclado.close();
    }

    private void buscarAutorPorNombre() {

        System.out.println("Ingrese el nombre completo o parcial del autor:");
        String nombreAutor = teclado.nextLine();

        List<Autor> autores = autorService.getAllAuthors().stream()
                .filter(autor -> autor.getNombre().toLowerCase().contains(nombreAutor.toLowerCase()))
                .collect(Collectors.toList());

        if (autores.isEmpty()) {
            System.out.println("No se encontró ningún autor con esa palabra clave en el nombre.");
        } else {
            System.out.println("\n--- Autores encontrados ---");
            autores.forEach(autor -> {
                System.out.println("ID: " + autor.getId());
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Nacimiento: " + (autor.getNacimiento() != null ? autor.getNacimiento() : "N/A"));
                System.out.println("Fallecimiento: " + (autor.getFallecimiento() != null ? autor.getFallecimiento() : "N/A"));

                if (autor.getLibros() != null && !autor.getLibros().isEmpty()) {
                    System.out.println("  Libros: " + autor.getLibros().stream().map(Libro::getTitulo).collect(Collectors.joining(", ")));
                }
                System.out.println("----------------------------------------");
            });
        }

    }

    private void listarLibros() {
        System.out.println("\n--- Libros registrados en la base de datos ---");
        List<Libro> libros = libroService.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
        } else {
            libros.forEach(libro -> {
                System.out.println("ID: " + libro.getId());
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + libro.getAutor());
                System.out.println("descargas: " + libro.getDescargas());

            });
        }

    }

    private void buscarLibro() {
        System.out.println("Ingrese al menos una Palabra Clave del Titulo");
        var tituloBuscado = teclado.nextLine();

        String json = consumoApi.obtenerDatos(BASE_URL + "?search=" + tituloBuscado.replace(" ", "+"));
        DatosRespuestaDTO respuestaDTO = conversor.obenerDatos(json, DatosRespuestaDTO.class);

        List<LibroDTO> librosDTOS = respuestaDTO.getResults();

        if (librosDTOS == null || librosDTOS.isEmpty()) {
            System.out.println("No se encontraron libros en la API para la búsqueda: '" + tituloBuscado + "'.");
            return;
        }

        System.out.println("\n--- Libros encontrados por la API Gutendex para '" + tituloBuscado + "' ---\n");

        librosDTOS.forEach(libroDTO -> {
            System.out.println("- Título: " + libroDTO.getTitulo() + libroDTO.getNumeroDescargas() + " (ID: " + libroDTO.getId_libro() + ")");
        });
        System.out.println("-----------------------------------------------------------------\n");

        boolean libroGuardado = false;

        for (LibroDTO libroDTO : librosDTOS) {
            if (libroDTO.getTitulo().toLowerCase().contains(tituloBuscado.toLowerCase())) {
                System.out.println("\n¡Coincidencia encontrada en los resultados de la API!");
                System.out.println("            ----------------------- ");
                System.out.println("Libro encontrado: '" + libroDTO.getTitulo() + "'");
                System.out.println("Idioma: " + (libroDTO.getIdiomas() != null && !libroDTO.getIdiomas().isEmpty() ? libroDTO.getIdiomas().get(0) : "N/A"));
                System.out.println("Descargas: " + libroDTO.getNumeroDescargas());

                if (libroDTO.getAutores() != null && !libroDTO.getAutores().isEmpty()) {
                    System.out.println("Autor(es): " + libroDTO.getAutores().stream()
                            .map(AutorDTO::getNombre)
                            .collect(Collectors.joining(", ")));
                    System.out.println("--------------------------------");
                    System.out.println("Si No es el libro que buscas y se encuentra en la Lista de arriba has una busqueda mas especifica -> OPCIÖN 2");
                }
                System.out.println("------------------------------------------");
                System.out.println("\n¿Qué quieres hacer con éste libro?");
                System.out.println("1. Guardar en la base de datos");
                System.out.println("2. Busqueda más específica(sin guardar)");
                System.out.print("Ingresá tu opción: ");

                String opcionAccionStr = teclado.nextLine();
                int opcionAccion;
                try {
                    opcionAccion = Integer.parseInt(opcionAccionStr);
                } catch (NumberFormatException e) {
                    System.out.println("Opción inválida. Volviendo al menú principal.");
                    return;
                }

                if (opcionAccion == 1) {
                    // Lógica para guardar el libro
                    Optional<Libro> libroExiste = libroService.findByTitle(libroDTO.getTitulo());

                    if (libroExiste.isPresent()) {
                        System.out.println("Libro '" + libroExiste.get().getTitulo() + "' ya está registrado en la base de datos.");
                        libroGuardado = true;
                        break;
                    } else {
                        Libro libro = new Libro();
                        libro.setTitulo(libroDTO.getTitulo());
                        libro.setDescargas(libroDTO.getNumeroDescargas());

                        if (libroDTO.getIdiomas() != null && !libroDTO.getIdiomas().isEmpty()) {
                            libro.setIdioma(libroDTO.getIdiomas().get(0));
                        } else {
                            libro.setIdioma("Desconocido");
                        }
                        libro.setDescargas(libroDTO.getNumeroDescargas());

                        if (libroDTO.getAutores() != null && !libroDTO.getAutores().isEmpty()) {
                            List<Autor> autoresEntidad = libroDTO.getAutores().stream()
                                    .map(autorDTO -> {
                                        Optional<Autor> autorExistente = autorService.findAuthorByName(autorDTO.getNombre());
                                        if (autorExistente.isPresent()) {
                                            return autorExistente.get();
                                        } else {
                                            Autor nuevoAutor = new Autor();
                                            nuevoAutor.setNombre(autorDTO.getNombre());
                                            nuevoAutor.setNacimiento(autorDTO.getNacimiento());
                                            nuevoAutor.setFallecimiento(autorDTO.getFallecimiento());
                                            return autorRepository.save(nuevoAutor);
                                        }
                                    })
                                    .collect(Collectors.toList());

                            libro.setAutor(autoresEntidad.isEmpty() ? null : autoresEntidad.get(0)); // Asigna la lista completa de autores
                        } else {
                            System.out.println("Advertencia: Libro '" + libroDTO.getTitulo() + "' sin información de autor en la API.");
                            libro.setAutor(new Autor());
                        }
                        Libro libroGuardadoEnDB = libroService.saveBook(libro);

                        System.out.println(libroGuardadoEnDB);

                        System.out.println(" (ID_GUTENDEX: " + libroDTO.getId_libro() + ") -> " + libro.getTitulo() + " guardado en la base de datos.");
                        libroGuardado = true;
                        break;
                    }
                } else if (opcionAccion == 2) {
                    System.out.println("Volviendo al menú principal sin guardar el libro.");
                    return;
                } else {
                    System.out.println("-----------------");
                    System.out.println("Opción inválida. Volviendo al menú principal.");
                    return;
                }
            }
        }
            if (!libroGuardado) {
                System.out.println("La Palabra Clave del Título: '" + tituloBuscado + "'es muy genérica!.");
                System.out.println("Si el libro que buscas está en la lista de arriba, intenta una búsqueda más exacta o elige la opción 2.");
            }

        }

    }



