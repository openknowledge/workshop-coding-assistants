import { useForm } from '@tanstack/react-form';
import { FormField } from '../../../components/FormField';
import { customerSchema, type CustomerFormData } from '../schemas/customerSchema';

interface CustomerFormProps {
  defaultValues?: CustomerFormData;
  onSubmit: (data: CustomerFormData) => void;
  isPending: boolean;
  submitLabel: string;
}

export function CustomerForm({
  defaultValues,
  onSubmit,
  isPending,
  submitLabel,
}: CustomerFormProps) {
  const form = useForm({
    defaultValues: defaultValues ?? {
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      birthDate: '',
    },
    onSubmit: async ({ value }) => {
      onSubmit(value);
    },
    validators: {
      onChange: customerSchema,
    },
  });

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        e.stopPropagation();
        form.handleSubmit();
      }}
      className="customer-form"
    >
      <form.Field name="firstName">
        {(field) => <FormField field={field} label="First Name" required />}
      </form.Field>

      <form.Field name="lastName">
        {(field) => <FormField field={field} label="Last Name" required />}
      </form.Field>

      <form.Field name="email">
        {(field) => <FormField field={field} label="Email" type="email" required />}
      </form.Field>

      <form.Field name="phoneNumber">
        {(field) => <FormField field={field} label="Phone Number" type="tel" />}
      </form.Field>

      <form.Field name="birthDate">
        {(field) => <FormField field={field} label="Date of Birth" type="date" />}
      </form.Field>

      <div className="form-actions">
        <form.Subscribe selector={(state) => [state.canSubmit, state.isSubmitting]}>
          {([canSubmit, isSubmitting]) => (
            <button
              type="submit"
              disabled={!canSubmit || isSubmitting || isPending}
              className="btn btn-primary"
            >
              {isPending ? 'Saving...' : submitLabel}
            </button>
          )}
        </form.Subscribe>
      </div>
    </form>
  );
}
