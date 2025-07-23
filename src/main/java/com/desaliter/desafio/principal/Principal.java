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

    // Instanciamos los servicios y repositorios de manera más limpia
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);
    private static final String BASE_URL = "https://gutendex.com/books/";

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public Principal(LibroService libroService, AutorService autorService,
                     LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroService = libroService;
        this.autorService = autorService;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        int option;
        // Uso de un "box" para el logo y un menú más ordenado
        String logo = """
               ╔════════════════════════════════════════════════════════════════════════════ ═══╗
               ║                                                                                ║
               ║     ,--.   ,--.  ,--.                  ,---.  ,--.                            ║
               ║     |  |   `--',-'  '-. ,---. ,--.--. /  O  \\ |  |,--.,--.,--.--.,--,--.       ║
               ║     |  |   ,--.'-.  .-'| .-. :|  .--'|  .-.  ||  ||  ||  ||  .--' ,-.  |      ║
               ║     |  '--.|  |  |  |  \\   --.|  |   |  | |  ||  |'  ''  '|  |  \\ '-'  |      ║
               ║     `-----'`--'  `--'   `----'`--'   `--' `--'`--' `----' `--'   `--`--'      ║
               ║                                                                                ║
               ╚════════════════════════════════════════════════════════════════════════════ ═══╝
               """;
        String menu = """
            --- Selecciona una opción ---
            -----------------------------------
            1. Buscar libro por título 📖
            2. Listar todos los libros 📚
            3. Buscar autores por nombre 👤
            4. Buscar autores vivos en un año 🗓️
            5. Buscar libros por idioma 🌐
            9. Salir ❌
            -----------------------------------
            """;
        do {
            System.out.println(logo);
            System.out.println(menu);
            System.out.print("Ingresa tu opción: ");

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
                        buscarAutoresVivosEnAnio();
                        break;
                    case 5:
                        buscarLibroPorIdioma();
                        break;
                    case 9:
                        System.out.println("----------------------------------------------");
                        System.out.println("✅ ¡Gracias por utilizar Gutendex-Alura-On-line! ✅");
                        System.out.println("----------------------------------------------");
                        break;
                    default:
                        System.out.println("❌ Opción inválida. Por favor, ingresa una opción del 1 al 5, o 9 para salir. ❌");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Error: Ingresa un número válido para la opción del menú. ❌");
                teclado.nextLine(); // Limpiar el buffer del scanner
                option = -1; // Mantener el bucle activo
            }
        } while (option != 9);
        teclado.close();
    }

    // --- LÓGICA DE BÚSQUEDA Y MENSAJES REFINADOS ---

    private void buscarLibroPorIdioma() {
        System.out.println("\n--- Búsqueda de Libros por Idioma ---");
        System.out.println("-------------------------------------");
        System.out.println("Ingresá el código del idioma (ej. 'es' para español, 'en' para inglés):");
        String idioma = teclado.nextLine().toLowerCase();

        // 1. Búsqueda en la Base de Datos Local
        System.out.println("\n🔍 Buscando libros en la base de datos local para el idioma '" + idioma + "'...");
        List<Libro> librosEnBD = libroService.findByLanguage(idioma);

        if (!librosEnBD.isEmpty()) {
            System.out.println("✅ ¡Libros encontrados en la base de datos local! ✅");
            imprimirLibros(librosEnBD);
        } else {
            System.out.println("❌ No se encontraron libros en la base de datos local para este idioma.");
        }

        // 2. Opción de búsqueda en la API
        System.out.println("\n-------------------------------------");
        System.out.println("❓ ¿Quieres buscar también en la API de Gutendex?");
        System.out.println("1. Sí, buscar en la API");
        System.out.println("2. No, volver al menú principal");
        System.out.print("Ingresa tu opción: ");

        int opcionAPI = leerOpcionEntero();
        if (opcionAPI == 1) {
            System.out.println("\n⏳ Buscando en la API de Gutendex para el idioma '" + idioma + "'...");
            String json = consumoApi.obtenerDatos(BASE_URL + "?languages=" + idioma);
            DatosRespuestaDTO respuestaDTO = conversor.obenerDatos(json, DatosRespuestaDTO.class);

            List<LibroDTO> librosAPI = respuestaDTO.getResults();
            if (librosAPI == null || librosAPI.isEmpty()) {
                System.out.println("❌ No se encontraron libros en la API para este idioma.");
                return;
            }

            System.out.println("\n✅ ¡Libros encontrados en la API! ✅");
            procesarLibrosDesdeAPI(librosAPI, "idioma");

        } else {
            System.out.println("Volviendo al menú principal.");
        }
    }

    private void buscarAutoresVivosEnAnio() {
        System.out.println("\n--- Búsqueda de Autores Vivos ---");
        System.out.println("---------------------------------");
        System.out.print("Ingrese un año positivo: ");
        int anio = leerOpcionEntero();

        List<Autor> autores = autorService.getAllAuthors();
        List<Autor> autoresVivos = autores.stream()
                .filter(autor -> autor.getNacimiento() != null && autor.getNacimiento() <= anio &&
                        (autor.getFallecimiento() == null || autor.getFallecimiento() >= anio))
                .collect(Collectors.toList());

        if (autoresVivos.isEmpty()) {
            System.out.println("\n❌ No se encontraron autores vivos en el año " + anio + " en la base de datos.");
        } else {
            System.out.println("\n✅ Autores vivos en el año " + anio + ":");
            imprimirAutores(autoresVivos);
        }
    }

    public void buscarAutorPorNombre() {
        System.out.println("\n--- Búsqueda de Autores por Nombre ---");
        System.out.println("-------------------------------------");
        System.out.print("Ingrese el nombre del autor a buscar: ");
        String nombre = teclado.nextLine();

        List<Autor> autores = autorService.getAllAuthors().stream()
                .filter(autor -> autor.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());

        if (autores.isEmpty()) {
            System.out.println("❌ No se encontraron autores con ese nombre en la base de datos.");
        } else {
            System.out.println("\n✅ Autores encontrados:");
            imprimirAutores(autores);
        }
    }

    private void listarLibros() {
        System.out.println("\n--- Libros Registrados en la Base de Datos ---");
        List<Libro> libros = libroService.findAll();

        if (libros.isEmpty()) {
            System.out.println("❌ No hay libros registrados en la base de datos.");
        } else {
            imprimirLibros(libros);
        }
    }

    private void buscarLibro() {
        System.out.println("\n--- Búsqueda de Libros por Título ---");
        System.out.println("-------------------------------------");
        System.out.print("Ingrese al menos una palabra clave del título: ");
        String tituloBuscado = teclado.nextLine();

        System.out.println("\n⏳ Buscando en la API de Gutendex... (Esto puede tardar un momento)");
        String json = consumoApi.obtenerDatos(BASE_URL + "?search=" + tituloBuscado.replace(" ", "+"));
        DatosRespuestaDTO respuestaDTO = conversor.obenerDatos(json, DatosRespuestaDTO.class);

        List<LibroDTO> librosDTOS = respuestaDTO.getResults();

        if (librosDTOS == null || librosDTOS.isEmpty()) {
            System.out.println("\n❌ No se encontraron libros en la API para la búsqueda: '" + tituloBuscado + "'.");
            return;
        }

        System.out.println("\n✅ Libros encontrados en la API para '" + tituloBuscado + "':");
        procesarLibrosDesdeAPI(librosDTOS, "titulo");
    }

    // --- MÉTODOS AUXILIARES PARA UN CÓDIGO MÁS LIMPIO Y REUTILIZABLE ---

    private void procesarLibrosDesdeAPI(List<LibroDTO> libros, String tipoBusqueda) {
        for (LibroDTO libroDTO : libros) {
            // Muestra los detalles de cada libro encontrado
            System.out.println("\n-------------------------------------------");
            System.out.println("✨ Coincidencia API: " + libroDTO.getTitulo());
            System.out.println("-------------------------------------------");
            System.out.println("📚 Título:     " + libroDTO.getTitulo());
            System.out.println("🌐 Idioma:     " + (libroDTO.getIdiomas().isEmpty() ? "N/A" : libroDTO.getIdiomas().get(0)));
            System.out.println("⬇️ Descargas:  " + libroDTO.getNumeroDescargas());
            if (libroDTO.getAutores() != null && !libroDTO.getAutores().isEmpty()) {
                System.out.println("✍️ Autor(es):   " + libroDTO.getAutores().stream()
                        .map(AutorDTO::getNombre)
                        .collect(Collectors.joining(", ")));
            } else {
                System.out.println("✍️ Autor(es):   No disponible");
            }
            System.out.println("-------------------------------------------");

            // Opciones para el usuario
            System.out.println("--- ¿Qué quieres hacer con este libro? ---");
            System.out.println("1. Guardar en la base de datos ✅");
            System.out.println("2. Ver el siguiente libro ⏭️");
            System.out.println("3. Volver al menú principal ❌");
            System.out.print("Ingresa tu opción: ");

            int opcion = leerOpcionEntero();
            if (opcion == 1) {
                guardarLibro(libroDTO);
                return;
            } else if (opcion == 3) {
                System.out.println("Volviendo al menú principal...");
                return;
            }
            // Si la opción es 2, el bucle continúa
        }
        System.out.println("\n--- Fin de la lista de resultados de la API. ---");
    }

    private void guardarLibro(LibroDTO libroDTO) {
        Optional<Libro> libroExiste = libroService.findByTitle(libroDTO.getTitulo());
        if (libroExiste.isPresent()) {
            System.out.println("\n❌ Libro '" + libroExiste.get().getTitulo() + "' ya está registrado en la base de datos.");
        } else {
            Libro libro = new Libro();
            libro.setTitulo(libroDTO.getTitulo());
            libro.setDescargas(libroDTO.getNumeroDescargas());
            libro.setIdioma(libroDTO.getIdiomas().isEmpty() ? "Desconocido" : libroDTO.getIdiomas().get(0));

            if (!libroDTO.getAutores().isEmpty()) {
                Autor autorPrincipal = procesarAutor(libroDTO.getAutores().get(0));
                libro.setAutor(autorPrincipal);
            } else {
                System.out.println("Advertencia: Libro sin información de autor.");
                libro.setAutor(null);
            }

            Libro libroGuardadoEnDB = libroService.saveBook(libro);
            System.out.println("\n✅ Libro guardado exitosamente: ");
            System.out.println("ID_GUTENDEX: " + libroDTO.getId_libro() + " -> " + libroGuardadoEnDB);
        }
    }

    private Autor procesarAutor(AutorDTO autorDTO) {
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
    }

    private void imprimirLibros(List<Libro> libros) {
        libros.forEach(libro -> {
            System.out.println("-------------------------------------------");
            System.out.println("📚 Título:     " + libro.getTitulo());
            System.out.println("👤 Autor:      " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "N/A"));
            System.out.println("🌐 Idioma:     " + libro.getIdioma());
            System.out.println("⬇️ Descargas:  " + libro.getDescargas());
        });
        System.out.println("-------------------------------------------\n");
    }

    private void imprimirAutores(List<Autor> autores) {
        autores.forEach(autor -> {
            System.out.println("-------------------------------------------");
            System.out.println("👤 Nombre:         " + autor.getNombre());
            System.out.println("🗓️ Nacimiento:     " + (autor.getNacimiento() != null ? autor.getNacimiento() : "N/A"));
            System.out.println("⚰️ Fallecimiento:  " + (autor.getFallecimiento() != null ? autor.getFallecimiento() : "N/A"));
            if (autor.getLibros() != null && !autor.getLibros().isEmpty()) {
                System.out.println("📚 Libros:         " + autor.getLibros().stream()
                        .map(Libro::getTitulo)
                        .collect(Collectors.joining("; ")));
            }
        });
        System.out.println("-------------------------------------------\n");
    }

    private int leerOpcionEntero() {
        try {
            int opcion = teclado.nextInt();
            teclado.nextLine();
            return opcion;
        } catch (InputMismatchException e) {
            System.out.println("❌ Entrada inválida. Se esperaba un número.");
            teclado.nextLine();
            return -1;
        }
    }
}



