import axios from 'axios';
import { getEnv } from '../utils/Env';
export const api = axios.create({ baseURL: getEnv().API_BASE_URL });
export const authHeaders = (header: string) => ({ Authorization: header });
export const videoPath = (id: string) => '/videos/' + encodeURIComponent(id);
