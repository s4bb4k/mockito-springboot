package com.example.testspringbootapp.springboot_test.repositories;

import com.example.testspringbootapp.springboot_test.models.Banco;

import java.util.List;

public interface BancoRepository {

    List<Banco> findAll();
    Banco findById(long id);
    void update (Banco banco);

}
