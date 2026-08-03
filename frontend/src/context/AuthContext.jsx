import { createContext, useContext, useEffect, useState } from 'react';
import apiClient, { setAccessToken } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    attemptSilentRefresh();
  }, []);

  async function attemptSilentRefresh() {
    const storedRefreshToken = localStorage.getItem('refreshToken');
    if (!storedRefreshToken) {
      setIsLoading(false);
      return;
    }
    try {
      const response = await apiClient.post('/auth/refresh', { refreshToken: storedRefreshToken });
      applyAuthResponse(response.data);
    } catch {
      localStorage.removeItem('refreshToken');
    } finally {
      setIsLoading(false);
    }
  }

  function applyAuthResponse(data) {
    setAccessToken(data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    setUser({ isAuthenticated: true });
  }

  async function login(username, password) {
    const response = await apiClient.post('/auth/login', { username, password });
    applyAuthResponse(response.data);
  }

  async function register(username, email, password) {
    const response = await apiClient.post('/auth/register', { username, email, password });
    applyAuthResponse(response.data);
  }

  async function logout() {
    const storedRefreshToken = localStorage.getItem('refreshToken');
    if (storedRefreshToken) {
      try {
        await apiClient.post('/auth/logout', { refreshToken: storedRefreshToken });
      } catch {
        // Clear local state regardless of whether the network call succeeded
      }
    }
    setAccessToken(null);
    localStorage.removeItem('refreshToken');
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}