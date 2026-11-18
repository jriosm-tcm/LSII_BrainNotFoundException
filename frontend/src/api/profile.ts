import { api, authHeaders } from './client';
export type Profile = { email: string; description: string; avatarUrl: string };
export const getProfile = (header: string) =>
  api.get<Profile>('/profile/me', { headers: authHeaders(header) });
export const updateProfile = (profile: Pick<Profile, 'description' | 'avatarUrl'>, header: string) =>
  api.put('/profile/me', profile, { headers: authHeaders(header) });
export const uploadAvatar = (file: File, header: string) => {
  const form = new FormData();
  form.append('file', file);
  return api.post<{ avatarUrl: string }>('/profile/me/avatar', form, { headers: authHeaders(header) });
};
