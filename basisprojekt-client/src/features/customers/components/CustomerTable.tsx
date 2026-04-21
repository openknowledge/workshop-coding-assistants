import { useState } from 'react';
import {
  createColumnHelper,
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  flexRender,
  type SortingState,
} from '@tanstack/react-table';
import { Link } from '@tanstack/react-router';
import type { CustomerResponse } from '../schemas/customerSchema';
import { useDeleteCustomer } from '../api/customerQueries';

const columnHelper = createColumnHelper<CustomerResponse>();

function ActionButtons({ customerId }: { customerId: number }) {
  const deleteMutation = useDeleteCustomer();

  const handleDelete = () => {
    if (window.confirm('Kunde wirklich loeschen?')) {
      deleteMutation.mutate(customerId);
    }
  };

  return (
    <div className="action-buttons">
      <Link
        to="/customers/$customerId"
        params={{ customerId: String(customerId) }}
        className="btn btn-edit"
      >
        Bearbeiten
      </Link>
      <button
        type="button"
        onClick={handleDelete}
        disabled={deleteMutation.isPending}
        className="btn btn-delete"
      >
        {deleteMutation.isPending ? 'Wird geloescht...' : 'Loeschen'}
      </button>
    </div>
  );
}

const columns = [
  columnHelper.accessor('firstName', { header: 'Vorname' }),
  columnHelper.accessor('lastName', { header: 'Nachname' }),
  columnHelper.accessor('email', { header: 'E-Mail' }),
  columnHelper.accessor('phoneNumber', {
    header: 'Telefon',
    cell: (info) => info.getValue() ?? '-',
  }),
  columnHelper.accessor('birthDate', {
    header: 'Geburtsdatum',
    cell: (info) => info.getValue() ?? '-',
  }),
  columnHelper.display({
    id: 'actions',
    header: 'Aktionen',
    cell: ({ row }) => <ActionButtons customerId={row.original.id} />,
  }),
];

interface CustomerTableProps {
  customers: CustomerResponse[];
}

export function CustomerTable({ customers }: CustomerTableProps) {
  const [sorting, setSorting] = useState<SortingState>([]);
  const [globalFilter, setGlobalFilter] = useState('');

  const table = useReactTable({
    data: customers,
    columns,
    state: {
      sorting,
      globalFilter,
    },
    onSortingChange: setSorting,
    onGlobalFilterChange: setGlobalFilter,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
  });

  return (
    <div className="table-container">
      <div className="table-toolbar">
        <input
          value={globalFilter ?? ''}
          onChange={(e) => setGlobalFilter(e.target.value)}
          placeholder="Suchen..."
          className="search-input"
        />
      </div>

      <table className="data-table">
        <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th
                  key={header.id}
                  onClick={header.column.getToggleSortingHandler()}
                  className={header.column.getCanSort() ? 'sortable' : ''}
                >
                  {flexRender(header.column.columnDef.header, header.getContext())}
                  {header.column.getIsSorted() === 'asc' ? ' \u25B2' : ''}
                  {header.column.getIsSorted() === 'desc' ? ' \u25BC' : ''}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="empty-row">
                Keine Kunden vorhanden
              </td>
            </tr>
          ) : (
            table.getRowModel().rows.map((row) => (
              <tr key={row.id}>
                {row.getVisibleCells().map((cell) => (
                  <td key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>

      <div className="pagination">
        <button
          type="button"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
        >
          Zurueck
        </button>
        <span>
          Seite {table.getState().pagination.pageIndex + 1} von {table.getPageCount()}
        </span>
        <button type="button" onClick={() => table.nextPage()} disabled={!table.getCanNextPage()}>
          Weiter
        </button>
      </div>
    </div>
  );
}
