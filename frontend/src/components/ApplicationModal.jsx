import { useState, useEffect } from 'react';

const STATUS_OPTIONS = ['APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'];

export default function ApplicationModal({ isOpen, onClose, onSubmit, initialData }) {
  const [companyName, setCompanyName] = useState('');
  const [jobTitle, setJobTitle] = useState('');
  const [salary, setSalary] = useState('');
  const [status, setStatus] = useState('APPLIED');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (initialData) {
      setCompanyName(initialData.companyName);
      setJobTitle(initialData.jobTitle);
      setSalary(initialData.salary);
      setStatus(initialData.status);
    } else {
      setCompanyName('');
      setJobTitle('');
      setSalary('');
      setStatus('APPLIED');
    }
    setError('');
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      await onSubmit({ companyName, jobTitle, salary: Number(salary), status });
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Something went wrong');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 px-4">
      <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-lg">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">
          {initialData ? 'Edit Application' : 'Add Application'}
        </h2>

        {error && <p className="mb-3 text-sm text-red-600">{error}</p>}

        <form onSubmit={handleSubmit} className="space-y-3">
          <input
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
            placeholder="Company name"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
          />
          <input
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
            placeholder="Job title"
            value={jobTitle}
            onChange={(e) => setJobTitle(e.target.value)}
          />
          <input
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
            type="number"
            min="0"
            placeholder="Salary"
            value={salary}
            onChange={(e) => setSalary(e.target.value)}
          />
          <select
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-gray-900 focus:outline-none"
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="rounded-lg px-4 py-2 text-sm text-gray-600 hover:bg-gray-100">
              Cancel
            </button>
            <button type="submit" disabled={isSubmitting} className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:opacity-50">
              {isSubmitting ? 'Saving...' : 'Save'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}