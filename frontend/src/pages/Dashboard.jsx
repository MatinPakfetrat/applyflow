import { useEffect, useState, useCallback } from 'react';
import apiClient from '../api/client';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../hooks/useToast';
import StatusBadge from '../components/StatusBadge';
import ApplicationModal from '../components/ApplicationModal';
import Toast from '../components/Toast';
import Logo from '../components/Logo';

const STATUS_OPTIONS = ['APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'];
const STATUS_BORDER = {
  APPLIED: 'border-l-status-applied',
  INTERVIEW: 'border-l-status-interview',
  OFFER: 'border-l-status-offer',
  REJECTED: 'border-l-status-rejected',
};
const PAGE_SIZE = 8;

export default function Dashboard() {
  const { logout } = useAuth();
  const { toast, showToast } = useToast();

  const [applications, setApplications] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');
  const [companyFilter, setCompanyFilter] = useState('');
  const [sort, setSort] = useState('createdAt,desc');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingApplication, setEditingApplication] = useState(null);

  const fetchApplications = useCallback(async () => {
    setIsLoading(true);
    setError('');
    try {
      const response = await apiClient.get('/applications', {
        params: {
          page,
          size: PAGE_SIZE,
          sort,
          ...(statusFilter && { status: statusFilter }),
          ...(companyFilter && { company: companyFilter }),
        },
      });
      setApplications(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch {
      setError('Could not load your applications. Try refreshing.');
    } finally {
      setIsLoading(false);
    }
  }, [page, sort, statusFilter, companyFilter]);

  useEffect(() => { fetchApplications(); }, [fetchApplications]);

  function openAddModal() {
    setEditingApplication(null);
    setIsModalOpen(true);
  }

  function openEditModal(application) {
    setEditingApplication(application);
    setIsModalOpen(true);
  }

  async function handleSave(formData) {
    if (editingApplication) {
      await apiClient.put(`/applications/${editingApplication.id}`, formData);
      showToast('Application updated');
    } else {
      await apiClient.post('/applications', formData);
      showToast('Application added');
    }
    fetchApplications();
  }

  async function handleDelete(app) {
    if (!window.confirm(`Delete your application to ${app.companyName}? This can't be undone.`)) return;
    await apiClient.delete(`/applications/${app.id}`);
    showToast('Application deleted');
    fetchApplications();
  }

  const hasFilters = statusFilter || companyFilter;

  return (
    <div className="min-h-screen bg-canvas">
      <header className="border-b border-hairline bg-white px-8 py-4">
        <div className="mx-auto flex max-w-5xl items-center justify-between">
          <div className="flex items-center gap-2.5">
            <Logo className="h-7 w-7" />
            <span className="text-lg font-semibold text-ink">ApplyFlow</span>
          </div>
          <button onClick={logout} className="text-sm text-muted hover:text-ink">Log out</button>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-8 py-8">
        <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
          <div className="flex flex-wrap gap-2">
            <select
              className="rounded-lg border border-hairline bg-white px-3 py-2 text-sm text-ink focus:border-ink focus:outline-none"
              value={statusFilter}
              onChange={(e) => { setPage(0); setStatusFilter(e.target.value); }}
            >
              <option value="">All statuses</option>
              {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>

            <input
              className="rounded-lg border border-hairline bg-white px-3 py-2 text-sm text-ink focus:border-ink focus:outline-none"
              placeholder="Filter by company"
              value={companyFilter}
              onChange={(e) => { setPage(0); setCompanyFilter(e.target.value); }}
            />

            <select
              className="rounded-lg border border-hairline bg-white px-3 py-2 text-sm text-ink focus:border-ink focus:outline-none"
              value={sort}
              onChange={(e) => { setPage(0); setSort(e.target.value); }}
            >
              <option value="createdAt,desc">Newest first</option>
              <option value="createdAt,asc">Oldest first</option>
              <option value="salary,desc">Salary: high to low</option>
              <option value="salary,asc">Salary: low to high</option>
            </select>
          </div>

          <button onClick={openAddModal} className="rounded-lg bg-ink px-4 py-2 text-sm font-medium text-white hover:bg-ink/90">
            + Add application
          </button>
        </div>

        {error && <p className="mb-4 text-sm text-status-rejected">{error}</p>}

        <div className="overflow-hidden rounded-xl border border-hairline bg-white">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-hairline bg-canvas text-xs uppercase tracking-wide text-muted">
              <tr>
                <th className="px-4 py-3">Company</th>
                <th className="px-4 py-3">Role</th>
                <th className="px-4 py-3">Salary</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-hairline">
              {isLoading && Array.from({ length: 4 }).map((_, i) => (
                <tr key={i}>
                  <td colSpan={5} className="px-4 py-4">
                    <div className="h-4 w-full animate-pulse rounded bg-hairline" />
                  </td>
                </tr>
              ))}

              {!isLoading && applications.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-16 text-center">
                    <p className="text-sm font-medium text-ink">
                      {hasFilters ? 'No applications match these filters' : 'Nothing tracked yet'}
                    </p>
                    <p className="mt-1 text-sm text-muted">
                      {hasFilters ? 'Try adjusting your filters.' : 'Add your first application to start tracking your search.'}
                    </p>
                    {!hasFilters && (
                      <button onClick={openAddModal} className="mt-4 rounded-lg bg-ink px-4 py-2 text-sm font-medium text-white hover:bg-ink/90">
                        + Add application
                      </button>
                    )}
                  </td>
                </tr>
              )}

              {!isLoading && applications.map((app) => (
                <tr key={app.id} className={`border-l-4 ${STATUS_BORDER[app.status]} hover:bg-canvas`}>
                  <td className="px-4 py-3 font-medium text-ink">{app.companyName}</td>
                  <td className="px-4 py-3 text-muted">{app.jobTitle}</td>
                  <td className="px-4 py-3 text-muted">${Number(app.salary).toLocaleString()}</td>
                  <td className="px-4 py-3"><StatusBadge status={app.status} /></td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => openEditModal(app)} className="mr-3 text-muted hover:text-ink">Edit</button>
                    <button onClick={() => handleDelete(app)} className="text-status-rejected hover:opacity-75">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="mt-4 flex items-center justify-center gap-3 text-sm">
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)} className="rounded-lg px-3 py-1.5 text-muted hover:bg-white disabled:opacity-40">
              Previous
            </button>
            <span className="text-muted">Page {page + 1} of {totalPages}</span>
            <button disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)} className="rounded-lg px-3 py-1.5 text-muted hover:bg-white disabled:opacity-40">
              Next
            </button>
          </div>
        )}
      </main>

      <ApplicationModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleSave} initialData={editingApplication} />
      <Toast toast={toast} />
    </div>
  );
}