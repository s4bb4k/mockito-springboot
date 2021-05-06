package com.example.testspringbootapp.springboot_test.repositories;

import com.example.testspringbootapp.springboot_test.models.Cuenta;

import java.util.List;

public interface CuentaRepository {

    List<Cuenta> findAll();
    Cuenta findById(long id);
    void update(Cuenta cuenta);

}
