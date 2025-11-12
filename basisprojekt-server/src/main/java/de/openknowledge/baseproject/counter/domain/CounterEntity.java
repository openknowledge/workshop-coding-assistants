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

import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.util.Objects.requireNonNull;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "Counter")
@Table(name = "TAB_COUNTER")
@NamedQuery(
        name = CounterEntity.BY_NAME,
        query = "SELECT c FROM Counter c WHERE c.counter.name.name = :name")
public class CounterEntity {

    public static final String BY_NAME = "CounterEntity.byName";

    @Id
    @Column(name = "C_ID")
    @GeneratedValue(strategy = SEQUENCE, generator = "SEQ_COUNTER")
    @SequenceGenerator(name = "SEQ_COUNTER", sequenceName = "SEQ_C_ID", allocationSize = 1)
    private Long id;

    @Embedded
    @AttributeOverride(name = "name.name", column = @Column(name = "C_NAME"))
    @AttributeOverride(name = "value.value", column = @Column(name = "C_VALUE"))
    private Counter counter;

    public CounterEntity(Counter counter) {
        this.counter = requireNonNull(counter);
    }

    protected CounterEntity() {
        // for JPA
    }

    public Counter getCounter() {
        return counter;
    }

    public void update(Counter newCounter) {
        counter = requireNonNull(newCounter);
    }
}
