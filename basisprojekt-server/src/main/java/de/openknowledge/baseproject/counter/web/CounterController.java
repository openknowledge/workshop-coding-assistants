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
package de.openknowledge.baseproject.counter.web;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.openknowledge.baseproject.counter.domain.Counter;
import de.openknowledge.baseproject.counter.domain.CounterName;
import de.openknowledge.baseproject.counter.domain.CounterService;
import de.openknowledge.baseproject.counter.domain.CounterValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/counters")
public class CounterController {

    @Autowired private CounterService counterService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public void createCounter(@RequestBody Counter counter) {
        counterService.createCounter(counter);
    }

    @GetMapping(path = "/{name}", produces = APPLICATION_JSON_VALUE)
    public CounterValue getCounter(@PathVariable("name") CounterName name) {
        return counterService
                .findCounter(name)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Counter not found"));
    }

    @PutMapping(path = "/{name}", consumes = APPLICATION_JSON_VALUE)
    public void updateCounter(
            @PathVariable("name") CounterName name, @RequestBody CounterValue value) {
        counterService.updateCounter(name, value);
    }
}
