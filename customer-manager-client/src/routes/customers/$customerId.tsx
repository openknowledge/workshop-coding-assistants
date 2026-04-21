import { useState } from 'react';
import { createFileRoute, useNavigate, Link } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import { CustomerForm } from '../../features/customers/components/CustomerForm';
import {
  customerQueryOptions,
  useUpdateCustomer,
} from '../../features/customers/api/customerQueries';

export const Route = createFileRoute('/customers/$customerId')({
  loader: ({ context: { queryClient }, params: { customerId } }) =>
    queryClient.ensureQueryData(customerQueryOptions(Number(customerId))),
  component: EditCustomerPage,
});

function EditCustomerPage() {
  const { customerId } = Route.useParams();
  const id = Number(customerId);
  const navigate = useNavigate();
  const { data: customer } = useSuspenseQuery(customerQueryOptions(id));
  const updateMutation = useUpdateCustomer(id);
  const [error, setError] = useState<string | null>(null);

  return (
    <div>
      <div className="page-header">
        <h2>Edit Customer</h2>
        <div className="action-buttons">
          <Link to="/customers" className="btn">
            Back
          </Link>
        </div>
      </div>
      {error && <div className="error-message">{error}</div>}
      <CustomerForm
        defaultValues={{
          firstName: customer.firstName,
          lastName: customer.lastName,
          email: customer.email,
          phoneNumber: customer.phoneNumber ?? '',
          birthDate: customer.birthDate ?? '',
        }}
        onSubmit={(data) => {
          setError(null);
          updateMutation.mutate(data, {
            onSuccess: () => navigate({ to: '/customers' }),
            onError: (err) => setError(err.message),
          });
        }}
        isPending={updateMutation.isPending}
        submitLabel="Save"
      />
    </div>
  );
}
