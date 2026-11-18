import { api, authHeaders } from './client';
export type Profile = { description: string; avatarUrl: string };
export const getProfile = (header: string) =>
  api.get<Profile>('/profile/me', { headers: authHeaders(header) });
export const updateProfile = (profile: Profile, header: string) =>
  api.put('/profile/me', profile, { headers: authHeaders(header) });
