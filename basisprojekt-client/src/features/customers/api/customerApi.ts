import type { CustomerFormData, CustomerResponse } from '../schemas/customerSchema';

const BASE_URL = '/api/customers';

export async function fetchCustomers(): Promise<CustomerResponse[]> {
  const response = await fetch(BASE_URL);
  if (!response.ok) {
    throw new Error('Kunden konnten nicht geladen werden');
  }
  return response.json();
}

export async function fetchCustomer(id: number): Promise<CustomerResponse> {
  const response = await fetch(`${BASE_URL}/${id}`);
  if (!response.ok) {
    throw new Error('Kunde nicht gefunden');
  }
  return response.json();
}

function sanitizeFormData(data: CustomerFormData): Record<string, unknown> {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    email: data.email,
    phoneNumber: data.phoneNumber || null,
    birthDate: data.birthDate || null,
  };
}

export async function createCustomer(data: CustomerFormData): Promise<CustomerResponse> {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(sanitizeFormData(data)),
  });
  if (!response.ok) {
    throw new Error('Kunde konnte nicht erstellt werden');
  }
  return response.json();
}

export async function updateCustomer(id: number, data: CustomerFormData): Promise<void> {
  const response = await fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(sanitizeFormData(data)),
  });
  if (!response.ok) {
    throw new Error('Kunde konnte nicht aktualisiert werden');
  }
}

export async function deleteCustomer(id: number): Promise<void> {
  const response = await fetch(`${BASE_URL}/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    throw new Error('Kunde konnte nicht geloescht werden');
  }
}
