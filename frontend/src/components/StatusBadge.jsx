const STATUS_COLORS = {
  APPLIED: 'bg-status-applied',
  INTERVIEW: 'bg-status-interview',
  OFFER: 'bg-status-offer',
  REJECTED: 'bg-status-rejected',
};

export default function StatusBadge({ status }) {
  return (
    <span className="inline-flex items-center gap-1.5 text-xs font-medium text-ink">
      <span className={`h-1.5 w-1.5 rounded-full ${STATUS_COLORS[status]}`} />
      {status}
    </span>
  );
}