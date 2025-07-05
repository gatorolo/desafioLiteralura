package com.desaliter.desafio.service;

import com.desaliter.desafio.model.Autor;
import com.desaliter.desafio.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AutorService {

    @Autowired
    AutorRepository autorRepository;

    public List<Autor> getAllAuthors() { return (List<Autor>) autorRepository.findAllBooks();}

    public List<Autor> findLiveAuthor(int anio) { return (List<Autor>) autorRepository.findAliveAuthor(anio);}

    public Autor saveAuthor(Autor autor) { return autorRepository.save(autor);}

    public Optional<Autor> findAuthorById(Long id) { return autorRepository.findById(id);}

    public Optional<Autor> findAuthorByName(String nombre) { return autorRepository.findByNombre(nombre);}

    public void deleteAuthor(Long id) { autorRepository.deleteById(id);}
}
