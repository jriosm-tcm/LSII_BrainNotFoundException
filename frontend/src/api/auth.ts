import axios from 'axios';
import { getEnv } from '../utils/Env';

export type AuthSession = {
  email: string;
  authorization: string;
};

type CurrentUserResponse = {
  email: string;
};

export async function login(email: string, password: string): Promise<AuthSession> {
  const bytes = new TextEncoder().encode(`${email}:${password}`);

  const credentials = Array.from(bytes, (byte) => String.fromCharCode(byte)).join('');

  const authorization = `Basic ${btoa(credentials)}`;

  const response = await axios.get<CurrentUserResponse>(`${getEnv().API_BASE_URL}/auth/me`, {
    headers: {
      Authorization: authorization,
    },
  });

  return {
    email: response.data.email,
    authorization,
  };
}
export type RegisterData = {
  username: string;
  email: string;
  password: string;
};

type RegisteredUser = {
  id: string;
  username: string;
  email: string;
};

export async function register(data: RegisterData): Promise<RegisteredUser> {
  const response = await axios.post<RegisteredUser>(`${getEnv().API_BASE_URL}/users/register`, data);

  return response.data;
}
