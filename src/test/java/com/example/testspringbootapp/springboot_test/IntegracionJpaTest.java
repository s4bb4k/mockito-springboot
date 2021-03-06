package com.example.testspringbootapp.springboot_test;

import com.example.testspringbootapp.springboot_test.models.Cuenta;
import com.example.testspringbootapp.springboot_test.repositories.CuentaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_jpa")
@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        // given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // when
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);
        // Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        // then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        // assertEquals(3, cuenta.getId());

    }

    @Test
    void testUpdate() {

        // given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // when
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);
        // Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        // then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        // assertEquals(3, cuenta.getId());

        // when
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        assertEquals("Pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("John", cuenta.getPersona());

        cuentaRepository.delete(cuenta);
        assertThrows(NoSuchElementException.class, () -> {
           // cuentaRepository.findByPersona("John").orElseThrow();
           cuentaRepository.findById(2L).orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());
    }
}
