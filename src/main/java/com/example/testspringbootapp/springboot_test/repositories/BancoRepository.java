package com.example.testspringbootapp.springboot_test.repositories;

import com.example.testspringbootapp.springboot_test.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BancoRepository extends JpaRepository<Banco, Long> {

   /* List<Banco> findAll();
    Banco findById(long id);
    void update (Banco banco);*/

}
