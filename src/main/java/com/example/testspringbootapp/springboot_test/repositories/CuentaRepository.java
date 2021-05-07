package com.example.testspringbootapp.springboot_test.repositories;

import com.example.testspringbootapp.springboot_test.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> findByPersona(String persona);


//    List<Cuenta> findAll();
//    Cuenta findById(long id);
//    void update(Cuenta cuenta);

}
