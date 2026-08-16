import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from '../components/Logo';

export default function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { register } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    try {
      await register(username, email, password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  }

  return (
    <div className="flex h-screen items-center justify-center bg-canvas">
      <form onSubmit={handleSubmit} className="w-full max-w-sm rounded-xl border border-hairline bg-white p-8 shadow-sm">
        <div className="mb-6 flex items-center gap-2.5">
          <Logo className="h-7 w-7" />
          <span className="text-lg font-semibold text-ink">ApplyFlow</span>
        </div>

        {error && <p className="mb-4 text-sm text-status-rejected">{error}</p>}

        <input
          className="mb-3 w-full rounded-lg border border-hairline px-3 py-2 text-sm text-ink focus:border-ink focus:outline-none"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          className="mb-3 w-full rounded-lg border border-hairline px-3 py-2 text-sm text-ink focus:border-ink focus:outline-none"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          className="mb-4 w-full rounded-lg border border-hairline px-3 py-2 text-sm text-ink focus:border-ink focus:outline-none"
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button className="w-full rounded-lg bg-ink py-2 text-sm font-medium text-white hover:bg-ink/90">
          Register
        </button>

        <p className="mt-4 text-center text-sm text-muted">
          Already have an account? <Link to="/login" className="font-medium text-ink underline">Log in</Link>
        </p>
      </form>
    </div>
  );
}