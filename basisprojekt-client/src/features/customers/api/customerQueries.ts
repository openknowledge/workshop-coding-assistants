import { queryOptions, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  fetchCustomers,
  fetchCustomer,
  createCustomer,
  updateCustomer,
  deleteCustomer,
} from './customerApi';
import type { CustomerFormData } from '../schemas/customerSchema';

export const customerKeys = {
  all: ['customers'] as const,
  detail: (id: number) => ['customers', id] as const,
};

export const customersQueryOptions = queryOptions({
  queryKey: customerKeys.all,
  queryFn: fetchCustomers,
});

export const customerQueryOptions = (id: number) =>
  queryOptions({
    queryKey: customerKeys.detail(id),
    queryFn: () => fetchCustomer(id),
  });

export function useCreateCustomer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CustomerFormData) => createCustomer(data),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: customerKeys.all });
    },
  });
}

export function useUpdateCustomer(id: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CustomerFormData) => updateCustomer(id, data),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: customerKeys.all });
      await queryClient.invalidateQueries({
        queryKey: customerKeys.detail(id),
      });
    },
  });
}

export function useDeleteCustomer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteCustomer(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: customerKeys.all });
    },
  });
}
