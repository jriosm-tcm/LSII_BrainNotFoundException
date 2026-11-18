import { api, authHeaders, videoPath } from './client';
import type { Video, VideoStats } from '../types';
export const getVideos = (query = '', signal?: AbortSignal) =>
  api.get<Video[]>(query.trim() ? '/videos/search' : '/videos',
    { params: query.trim() ? { q: query.trim() } : undefined, signal });
export const uploadVideo = (form: FormData, header: string) =>
  api.post('/videos/upload', form, { headers: authHeaders(header) });
export const editVideo = (id: string, title: string, description: string, header: string) =>
  api.put(videoPath(id) + '/metadata', { title, description }, { headers: authHeaders(header) });
export const getVideoStats = (id: string) => api.get<VideoStats>(videoPath(id) + '/stats');
export const trackVideoOpen = (id: string) => api.post(videoPath(id) + '/stats/view');

export const getMyVideos = (header: string) => api.get<Video[]>('/videos/mine', { headers: authHeaders(header) });
