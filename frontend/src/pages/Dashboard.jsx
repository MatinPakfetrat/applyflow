import { useAuth } from '../context/AuthContext';

export default function Dashboard() {
  const { logout } = useAuth();
  return (
    <div className="p-8">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-gray-900">ApplyFlow</h1>
        <button onClick={logout} className="text-sm text-gray-500 hover:text-gray-900">
          Log out
        </button>
      </div>
      <p className="mt-6 text-gray-500">Applications list coming next.</p>
    </div>
  );
}