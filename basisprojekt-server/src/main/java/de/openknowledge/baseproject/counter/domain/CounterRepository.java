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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CounterRepository {

    @PersistenceContext private EntityManager entityManager;

    public void persist(CounterEntity counter) {
        entityManager.persist(counter);
    }

    public Optional<CounterEntity> find(CounterName name) {
        return entityManager
                .createNamedQuery(CounterEntity.BY_NAME, CounterEntity.class)
                .setParameter("name", name.name())
                .getResultStream()
                .findFirst();
    }
}
