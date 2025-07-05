package com.desaliter.desafio.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String idioma;
    private String genero;
    private int descargas;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", idioma='" + idioma + '\'' +
                ", genero='" + genero + '\'' +
                ", descargas=" + descargas +
                ", autor=" + autor ;
    }
}

