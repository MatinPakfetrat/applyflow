const STATUS_STYLES = {
  APPLIED: 'bg-blue-50 text-blue-700 ring-blue-600/20',
  INTERVIEW: 'bg-amber-50 text-amber-700 ring-amber-600/20',
  OFFER: 'bg-green-50 text-green-700 ring-green-600/20',
  REJECTED: 'bg-red-50 text-red-700 ring-red-600/20',
};

export default function StatusBadge({ status }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${STATUS_STYLES[status]}`}>
      {status}
    </span>
  );
}