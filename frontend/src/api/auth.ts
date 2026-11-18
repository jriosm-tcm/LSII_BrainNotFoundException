import { api, authHeaders } from './client';
export const register = (email: string, password: string) => api.post('/auth/register', { email, password });
export const getCurrentUser = (header: string) => api.get<{ email: string }>('/auth/me', { headers: authHeaders(header) });
