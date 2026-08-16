export default function Toast({ toast }) {
  if (!toast) return null;

  const styles = toast.variant === 'error' ? 'bg-status-rejected' : 'bg-ink';

  return (
    <div className={`fixed bottom-6 left-1/2 -translate-x-1/2 rounded-lg px-4 py-2.5 text-sm font-medium text-white shadow-lg ${styles}`}>
      {toast.message}
    </div>
  );
}