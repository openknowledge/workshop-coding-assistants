import { z } from 'zod';

export const customerSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(255, 'First name must be at most 255 characters long'),
  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(255, 'Last name must be at most 255 characters long'),
  email: z
    .string()
    .min(1, 'Email is required')
    .email('Invalid email address')
    .max(255, 'Email must be at most 255 characters long'),
  phoneNumber: z
    .string()
    .max(50, 'Phone number must be at most 50 characters long')
    .optional()
    .or(z.literal('')),
  birthDate: z.string().optional().or(z.literal('')),
});

export type CustomerFormData = z.infer<typeof customerSchema>;

export interface CustomerResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string | null;
  birthDate: string | null;
}
