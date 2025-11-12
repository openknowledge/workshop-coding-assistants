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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "spring.flyway.enabled=true")
class CounterControllerTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort private int port;

    @Autowired private RestClient.Builder clientBuilder;

    @BeforeEach
    void setupRestClient() {
        clientBuilder.baseUrl("http://localhost:" + port + "/api");
    }

    @Test
    void updateCounter() {
        // Given
        RestClient client = clientBuilder.build();
        var createResponse =
                client.post()
                        .uri("/counters")
                        .contentType(APPLICATION_JSON)
                        .body(
                                """
                                {
                                    "name": "test",
                                    "value": 0
                                }
                                """)
                        .retrieve()
                        .toBodilessEntity();
        assertTrue(createResponse.getStatusCode().is2xxSuccessful(), "Creation successfull");

        // When
        var response = client.put().uri("/counters/test").body(42).retrieve().toBodilessEntity();
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Update successfull");

        // Then
        int updatedCount =
                client.get()
                        .uri("/counters/test")
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(Integer.class);
        assertThat(updatedCount).isEqualTo(42);
    }
}
