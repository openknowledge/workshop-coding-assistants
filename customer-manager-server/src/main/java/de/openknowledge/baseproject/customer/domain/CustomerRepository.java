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
package de.openknowledge.baseproject.customer.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    @PersistenceContext private EntityManager entityManager;

    public List<CustomerEntity> findAll() {
        return entityManager
                .createNamedQuery(CustomerEntity.FIND_ALL, CustomerEntity.class)
                .getResultList();
    }

    public Optional<CustomerEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(CustomerEntity.class, id));
    }

    public Optional<CustomerEntity> findByEmail(Email email) {
        return entityManager
                .createNamedQuery(CustomerEntity.BY_EMAIL, CustomerEntity.class)
                .setParameter("email", email.email())
                .getResultStream()
                .findFirst();
    }

    public void persist(CustomerEntity customer) {
        entityManager.persist(customer);
    }
}
