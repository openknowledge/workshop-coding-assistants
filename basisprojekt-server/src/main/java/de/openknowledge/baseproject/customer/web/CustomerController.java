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
package de.openknowledge.baseproject.customer.web;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.openknowledge.baseproject.customer.domain.Customer;
import de.openknowledge.baseproject.customer.domain.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/customers")
public class CustomerController {

    @Autowired private CustomerService customerService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<CustomerResponse> getCustomers() {
        return customerService.findAllCustomers().stream().map(CustomerResponse::from).toList();
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public CustomerResponse getCustomer(@PathVariable("id") Long id) {
        return customerService
                .findCustomer(id)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Customer not found"));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public CustomerResponse createCustomer(@Valid @RequestBody Customer customer) {
        try {
            return CustomerResponse.from(customerService.createCustomer(customer));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(CONFLICT, e.getMessage());
        }
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public void updateCustomer(@PathVariable("id") Long id, @Valid @RequestBody Customer customer) {
        try {
            customerService.updateCustomer(id, customer);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(NOT_FOUND, "Customer not found");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCustomer(@PathVariable("id") Long id) {
        try {
            customerService.deleteCustomer(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(NOT_FOUND, "Customer not found");
        }
    }
}
