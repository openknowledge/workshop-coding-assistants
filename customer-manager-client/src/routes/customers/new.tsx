import { useState } from 'react';
import { createFileRoute, useNavigate, Link } from '@tanstack/react-router';
import { CustomerForm } from '../../features/customers/components/CustomerForm';
import { useCreateCustomer } from '../../features/customers/api/customerQueries';

export const Route = createFileRoute('/customers/new')({
  component: NewCustomerPage,
});

function NewCustomerPage() {
  const navigate = useNavigate();
  const createMutation = useCreateCustomer();
  const [error, setError] = useState<string | null>(null);

  return (
    <div>
      <div className="page-header">
        <h2>New Customer</h2>
        <Link to="/customers" className="btn">
          Back
        </Link>
      </div>
      {error && <div className="error-message">{error}</div>}
      <CustomerForm
        onSubmit={(data) => {
          setError(null);
          createMutation.mutate(data, {
            onSuccess: () => navigate({ to: '/customers' }),
            onError: (err) => setError(err.message),
          });
        }}
        isPending={createMutation.isPending}
        submitLabel="Create"
      />
    </div>
  );
}
