import { useEffect, useState, useCallback } from 'react';
import apiClient from '../api/client';
import { useAuth } from '../context/AuthContext';
import StatusBadge from '../components/StatusBadge';
import ApplicationModal from '../components/ApplicationModal';

const STATUS_OPTIONS = ['APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'];
const PAGE_SIZE = 8;

export default function Dashboard() {
  const { logout } = useAuth();

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
      setError('Could not load applications');
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
    } else {
      await apiClient.post('/applications', formData);
    }
    fetchApplications();
  }

  async function handleDelete(id) {
    if (!window.confirm('Delete this application?')) return;
    await apiClient.delete(`/applications/${id}`);
    fetchApplications();
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="border-b border-gray-200 bg-white px-8 py-4">
        <div className="mx-auto flex max-w-5xl items-center justify-between">
          <h1 className="text-lg font-semibold text-gray-900">ApplyFlow</h1>
          <button onClick={logout} className="text-sm text-gray-500 hover:text-gray-900">Log out</button>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-8 py-8">
        <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
          <div className="flex flex-wrap gap-2">
            <select
              className="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
              value={statusFilter}
              onChange={(e) => { setPage(0); setStatusFilter(e.target.value); }}
            >
              <option value="">All statuses</option>
              {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>

            <input
              className="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
              placeholder="Filter by company"
              value={companyFilter}
              onChange={(e) => { setPage(0); setCompanyFilter(e.target.value); }}
            />

            <select
              className="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
              value={sort}
              onChange={(e) => { setPage(0); setSort(e.target.value); }}
            >
              <option value="createdAt,desc">Newest first</option>
              <option value="createdAt,asc">Oldest first</option>
              <option value="salary,desc">Salary: high to low</option>
              <option value="salary,asc">Salary: low to high</option>
            </select>
          </div>

          <button onClick={openAddModal} className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800">
            + Add Application
          </button>
        </div>

        {error && <p className="mb-4 text-sm text-red-600">{error}</p>}

        <div className="overflow-hidden rounded-xl border border-gray-200 bg-white">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-gray-200 bg-gray-50 text-xs uppercase text-gray-500">
              <tr>
                <th className="px-4 py-3">Company</th>
                <th className="px-4 py-3">Role</th>
                <th className="px-4 py-3">Salary</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {isLoading && <tr><td colSpan={5} className="px-4 py-6 text-center text-gray-400">Loading...</td></tr>}
              {!isLoading && applications.length === 0 && (
                <tr><td colSpan={5} className="px-4 py-6 text-center text-gray-400">No applications yet</td></tr>
              )}
              {applications.map((app) => (
                <tr key={app.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-900">{app.companyName}</td>
                  <td className="px-4 py-3 text-gray-600">{app.jobTitle}</td>
                  <td className="px-4 py-3 text-gray-600">${Number(app.salary).toLocaleString()}</td>
                  <td className="px-4 py-3"><StatusBadge status={app.status} /></td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => openEditModal(app)} className="mr-3 text-gray-500 hover:text-gray-900">Edit</button>
                    <button onClick={() => handleDelete(app.id)} className="text-red-500 hover:text-red-700">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="mt-4 flex items-center justify-center gap-3 text-sm">
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)} className="rounded-lg px-3 py-1.5 text-gray-600 hover:bg-gray-100 disabled:opacity-40">
              Previous
            </button>
            <span className="text-gray-500">Page {page + 1} of {totalPages}</span>
            <button disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)} className="rounded-lg px-3 py-1.5 text-gray-600 hover:bg-gray-100 disabled:opacity-40">
              Next
            </button>
          </div>
        )}
      </main>

      <ApplicationModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleSave} initialData={editingApplication} />
    </div>
  );
}