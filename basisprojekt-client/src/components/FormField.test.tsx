import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { FormField } from './FormField';

/**
 * TanStack Form + Zod (Standard Schema) returns validation errors as objects,
 * not plain strings. Each error is a StandardSchemaV1Issue with shape:
 *   { message: string, path: string[], code: string, ... }
 *
 * Using .join(', ') on these objects produces "[object Object]" instead of
 * the actual error message. The FormField component must extract .message
 * from each error object.
 */
describe('FormField', () => {
  function createMockField(overrides: Record<string, unknown> = {}) {
    return {
      name: 'testField',
      state: {
        value: '',
        meta: {
          errors: [],
          ...overrides,
        },
      },
      handleChange: () => {},
      handleBlur: () => {},
    };
  }

  it('renders label and input', () => {
    const field = createMockField();
    render(<FormField field={field} label="Vorname" />);

    expect(screen.getByLabelText('Vorname')).toBeInTheDocument();
  });

  it('renders required marker when required', () => {
    const field = createMockField();
    render(<FormField field={field} label="Vorname" required />);

    expect(screen.getByText(/Vorname/)).toHaveTextContent('Vorname *');
  });

  it('shows no error when errors array is empty', () => {
    const field = createMockField({ errors: [] });
    render(<FormField field={field} label="Vorname" />);

    expect(document.querySelector('.field-error')).not.toBeInTheDocument();
    expect(screen.queryByText(/erforderlich/)).not.toBeInTheDocument();
  });

  it('extracts .message from StandardSchemaV1Issue error objects (NOT [object Object])', () => {
    // This is the exact error shape that Zod returns via Standard Schema,
    // which TanStack Form passes through to field.state.meta.errors
    const zodStandardSchemaErrors = [
      {
        code: 'too_small',
        minimum: 1,
        type: 'string',
        inclusive: true,
        exact: false,
        message: 'Vorname ist erforderlich',
        path: ['firstName'],
      },
    ];

    const field = createMockField({ errors: zodStandardSchemaErrors });
    render(<FormField field={field} label="Vorname" required />);

    // The actual error message must be displayed
    expect(screen.getByText('Vorname ist erforderlich')).toBeInTheDocument();

    // Must NOT display [object Object]
    expect(screen.queryByText('[object Object]')).not.toBeInTheDocument();
  });

  it('handles multiple error objects on the same field', () => {
    const multipleErrors = [
      {
        code: 'too_small',
        minimum: 1,
        message: 'E-Mail ist erforderlich',
        path: ['email'],
      },
      {
        code: 'invalid_string',
        validation: 'email',
        message: 'Ungueltige E-Mail-Adresse',
        path: ['email'],
      },
    ];

    const field = createMockField({ errors: multipleErrors });
    render(<FormField field={field} label="E-Mail" required />);

    expect(
      screen.getByText('E-Mail ist erforderlich, Ungueltige E-Mail-Adresse')
    ).toBeInTheDocument();
  });

  it('handles plain string errors (fallback)', () => {
    const field = createMockField({ errors: ['This is a plain string error'] });
    render(<FormField field={field} label="Name" />);

    expect(screen.getByText('This is a plain string error')).toBeInTheDocument();
  });

  it('renders correct input type', () => {
    const field = createMockField();
    render(<FormField field={field} label="E-Mail" type="email" />);

    expect(screen.getByLabelText('E-Mail')).toHaveAttribute('type', 'email');
  });
});
