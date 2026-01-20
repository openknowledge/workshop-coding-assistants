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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import de.openknowledge.baseproject.infrastructure.test.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

@IntegrationTest
class RollbackTest {

    @LocalServerPort private int port;

    @Autowired private RestClient.Builder clientBuilder;

    @BeforeEach
    void setupRestClient() {
        clientBuilder.baseUrl("http://localhost:" + port + "/api");
    }

    @Test
    void updateCounterOnce() {
        // Given
        RestClient client = clientBuilder.build();
        int count =
                client.get()
                        .uri("/counters/example")
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(Integer.class);
        assertEquals(42, count);

        // When
        var response = client.put().uri("/counters/example").body(43).retrieve().toBodilessEntity();
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Update successfull");

        // Then
        int updatedCount =
                client.get()
                        .uri("/counters/example")
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(Integer.class);
        assertThat(updatedCount).isEqualTo(43);
    }

    @Test
    void updateCounterTwice() {
        // Given
        RestClient client = clientBuilder.build();
        int count =
                client.get()
                        .uri("/counters/example")
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(Integer.class);
        assertEquals(42, count);

        // When
        var response = client.put().uri("/counters/example").body(43).retrieve().toBodilessEntity();
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Update successfull");

        // Then
        int updatedCount =
                client.get()
                        .uri("/counters/example")
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(Integer.class);
        assertThat(updatedCount).isEqualTo(43);
    }
}
