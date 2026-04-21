import { createFileRoute, Link } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import { customersQueryOptions } from '../../features/customers/api/customerQueries';
import { CustomerTable } from '../../features/customers/components/CustomerTable';

export const Route = createFileRoute('/customers/')({
  loader: ({ context: { queryClient } }) => queryClient.ensureQueryData(customersQueryOptions),
  component: CustomerListPage,
});

function CustomerListPage() {
  const { data: customers } = useSuspenseQuery(customersQueryOptions);

  return (
    <div>
      <div className="page-header">
        <h2>Kunden</h2>
        <Link to="/customers/new" className="btn btn-primary">
          Neuer Kunde
        </Link>
      </div>
      <CustomerTable customers={customers} />
    </div>
  );
}
