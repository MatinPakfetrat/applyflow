export default function Logo({ className = 'h-8 w-8' }) {
  return (
    <svg viewBox="0 0 32 32" className={className}>
      <rect width="32" height="32" rx="8" fill="#18181B" />
      <circle cx="9" cy="16" r="3" fill="#2563EB" />
      <circle cx="16" cy="16" r="3" fill="#CA8A04" />
      <circle cx="23" cy="16" r="3" fill="#059669" />
    </svg>
  );
}