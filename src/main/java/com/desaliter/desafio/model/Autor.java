package com.desaliter.desafio.model;

import com.desaliter.desafio.dto.AutorDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer nacimiento;
    private Integer fallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Libro> libros = new ArrayList<>();

    public Autor(AutorDTO autorDTO) {
        this.nombre = String.valueOf(autorDTO.getNombre());
        this.nacimiento = Integer.valueOf(autorDTO.getNacimiento());
        this.fallecimiento = Integer.valueOf(autorDTO.getFallecimiento());
    }

    @Override
    public String toString() {
        return
                " nombre='" + nombre + '\'' +
                ", nacimiento=" + nacimiento +
                ", fallecimiento=" + fallecimiento;
    }
}
