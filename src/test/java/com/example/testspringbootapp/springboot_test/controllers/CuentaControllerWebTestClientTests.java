package com.example.testspringbootapp.springboot_test.controllers;

import com.example.testspringbootapp.springboot_test.models.Cuenta;
import com.example.testspringbootapp.springboot_test.models.TransaccionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTests {

    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testTransferir() throws JsonProcessingException {

        // Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);

        // when
        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()

                // then
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con exito", json.path("mensaje").asText());
                        assertEquals(1, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .jsonPath("$.mensaje").isNotEmpty()
                // .jsonPath("$.mensaje").value(is("Transferencia realizada con exito"))
                .jsonPath("$.mensaje").value(valor -> assertEquals("", valor))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .jsonPath(objectMapper.writeValueAsString(response));
    }

    @Test
    void testDetalle() throws JsonProcessingException {

        Cuenta cuenta = new Cuenta(1L, "andres", new BigDecimal("900"));
        client.get().uri("/api/cuenta/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("andres")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    void testDetalle2() {
        client.get().uri("/api/cuenta/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta cuenta = response.getResponseBody();
                    assertEquals("Jhon", cuenta.getPersona());
                    assertEquals("2000.00", cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    void testListar() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].persona").isEqualTo("jhon")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    void testListar2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("andres", cuentas.get(0).getPersona());
                    assertEquals(900, cuentas.get(0).getSaldo().intValue());
                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("jhon", cuentas.get(1).getPersona());
                    assertEquals(2100.00, cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    void testGuardar() {

        // Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // When
        client.post().uri("/api/cuentas/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
        // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.saldo").isEqualTo(3000);

    }

    @Test
    void testGuardar2() {

        // Given
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));

        // When
        client.post().uri("/api/cuentas/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta c = response.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPersona());
                    assertEquals("3500", c.getSaldo().toPlainString());
                });

    }

    @Test
    void testEliminar() {

        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/3").exchange()
//                .expectStatus().is5xxServerError();
                  .expectStatus().isNotFound()
                  .expectBody().isEmpty();

    }
}