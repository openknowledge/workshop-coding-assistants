import { z } from 'zod';

export const customerSchema = z.object({
  firstName: z
    .string()
    .min(1, 'Vorname ist erforderlich')
    .max(255, 'Vorname darf maximal 255 Zeichen lang sein'),
  lastName: z
    .string()
    .min(1, 'Nachname ist erforderlich')
    .max(255, 'Nachname darf maximal 255 Zeichen lang sein'),
  email: z
    .string()
    .min(1, 'E-Mail ist erforderlich')
    .email('Ungueltige E-Mail-Adresse')
    .max(255, 'E-Mail darf maximal 255 Zeichen lang sein'),
  phoneNumber: z
    .string()
    .max(50, 'Telefonnummer darf maximal 50 Zeichen lang sein')
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
