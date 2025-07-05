package com.desaliter.desafio.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutorDTO {
    @JsonAlias("name")  String nombre;
    @JsonAlias("birth_year") int nacimiento;
    @JsonAlias("death_year") int fallecimiento;
}
