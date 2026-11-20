import axios from 'axios';
import { getEnv } from '../utils/Env';

export type Profile = {
  id: string;
  username: string;
  email: string;
  description: string;
  avatarUrl: string;
};

export async function getProfile(authorization: string, signal?: AbortSignal): Promise<Profile> {
  const response = await axios.get<Profile>(`${getEnv().API_BASE_URL}/profile/me`, {
    headers: {
      Authorization: authorization,
    },
    signal,
  });

  return response.data;
}

export async function updateProfile(description: string, authorization: string): Promise<Profile> {
  const response = await axios.put<Profile>(
    `${getEnv().API_BASE_URL}/profile/me`,
    {
      description,
    },
    {
      headers: {
        Authorization: authorization,
      },
    }
  );

  return response.data;
}

export async function uploadAvatar(file: File, authorization: string): Promise<Profile> {
  const formData = new FormData();

  formData.append('file', file);

  const response = await axios.post<Profile>(`${getEnv().API_BASE_URL}/profile/me/avatar`, formData, {
    headers: {
      Authorization: authorization,
    },
  });

  return response.data;
}
