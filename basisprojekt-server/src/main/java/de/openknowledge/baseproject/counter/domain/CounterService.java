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
package de.openknowledge.baseproject.counter.domain;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CounterService {

    @Autowired private CounterRepository repository;

    public void createCounter(Counter counter) {
        repository.persist(new CounterEntity(counter));
    }

    public Optional<CounterValue> findCounter(CounterName name) {
        return repository.find(name).map(CounterEntity::getCounter).map(Counter::value);
    }

    public void updateCounter(CounterName name, CounterValue value) {
        repository
                .find(name)
                .ifPresentOrElse(
                        counter -> counter.update(new Counter(name, value)),
                        EntityNotFoundException::new);
    }
}
