/*
 * Copyright (C) open knowledge GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.baseproject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "spring.flyway.enabled=true")
class CustomerControllerTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort private int port;

    @Autowired private RestClient.Builder clientBuilder;

    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setupRestClient() {
        clientBuilder.baseUrl("http://localhost:" + port + "/api");
    }

    @Test
    void createAndGetCustomer() throws Exception {
        // Given
        RestClient client = clientBuilder.build();

        // When
        String createBody =
                client.post()
                        .uri("/customers")
                        .contentType(APPLICATION_JSON)
                        .body(
                                """
                                {
                                    "firstName": "Max",
                                    "lastName": "Mustermann",
                                    "email": "max@example.com",
                                    "phoneNumber": "+49 123 4567890",
                                    "birthDate": "1990-05-15"
                                }
                                """)
                        .retrieve()
                        .body(String.class);

        // Then
        JsonNode created = objectMapper.readTree(createBody);
        assertThat(created.get("id")).isNotNull();
        assertThat(created.get("firstName").asText()).isEqualTo("Max");
        assertThat(created.get("lastName").asText()).isEqualTo("Mustermann");
        assertThat(created.get("email").asText()).isEqualTo("max@example.com");

        // When -- get by id
        Long id = created.get("id").asLong();
        String getBody =
                client.get()
                        .uri("/customers/" + id)
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(String.class);

        // Then
        JsonNode fetched = objectMapper.readTree(getBody);
        assertThat(fetched.get("firstName").asText()).isEqualTo("Max");
        assertThat(fetched.get("email").asText()).isEqualTo("max@example.com");
    }

    @Test
    void listCustomers() throws Exception {
        // Given
        RestClient client = clientBuilder.build();
        var createResponse =
                client.post()
                        .uri("/customers")
                        .contentType(APPLICATION_JSON)
                        .body(
                                """
                                {
                                    "firstName": "Erika",
                                    "lastName": "Musterfrau",
                                    "email": "erika@example.com"
                                }
                                """)
                        .retrieve()
                        .toBodilessEntity();
        assertTrue(createResponse.getStatusCode().is2xxSuccessful(), "Creation successful");

        // When
        String listBody =
                client.get()
                        .uri("/customers")
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(String.class);

        // Then
        assertThat(listBody).contains("erika@example.com");
    }

    @Test
    void updateCustomer() throws Exception {
        // Given
        RestClient client = clientBuilder.build();
        String createBody =
                client.post()
                        .uri("/customers")
                        .contentType(APPLICATION_JSON)
                        .body(
                                """
                                {"firstName":"Before","lastName":"Update","email":"update-test@example.com"}
                                """)
                        .retrieve()
                        .body(String.class);
        Long id = objectMapper.readTree(createBody).get("id").asLong();

        // When
        var updateResponse =
                client.put()
                        .uri("/customers/" + id)
                        .contentType(APPLICATION_JSON)
                        .body(
                                """
                                {"firstName":"After","lastName":"Update","email":"update-test@example.com"}
                                """)
                        .retrieve()
                        .toBodilessEntity();
        assertTrue(updateResponse.getStatusCode().is2xxSuccessful(), "Update successful");

        // Then
        String getBody =
                client.get()
                        .uri("/customers/" + id)
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(String.class);
        JsonNode updated = objectMapper.readTree(getBody);
        assertThat(updated.get("firstName").asText()).isEqualTo("After");
    }

    @Test
    void deleteCustomer() throws Exception {
        // Given
        RestClient client = clientBuilder.build();
        String createBody =
                client.post()
                        .uri("/customers")
                        .contentType(APPLICATION_JSON)
                        .body(
                                """
                                {
                                    "firstName": "ToDelete",
                                    "lastName": "User",
                                    "email": "delete-test@example.com"
                                }
                                """)
                        .retrieve()
                        .body(String.class);
        Long id = objectMapper.readTree(createBody).get("id").asLong();

        // When
        var deleteResponse = client.delete().uri("/customers/" + id).retrieve().toBodilessEntity();
        assertTrue(deleteResponse.getStatusCode().is2xxSuccessful(), "Delete successful");

        // Then -- GET should return 404
        var notFoundResponse =
                client.get()
                        .uri("/customers/" + id)
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.value() == 404, (req, res) -> {})
                        .toBodilessEntity();
        assertThat(notFoundResponse.getStatusCode().value()).isEqualTo(404);
    }
}
