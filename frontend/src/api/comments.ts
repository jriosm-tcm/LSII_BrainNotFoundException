import { api, authHeaders, videoPath } from './client';
import type { VideoComment } from '../types';
const base = (id: string) => videoPath(id) + '/comments';
export const getComments = (id: string) => api.get<VideoComment[]>(base(id));
export const postComment = (id: string, content: string, header: string) =>
  api.post<VideoComment>(base(id), { content }, { headers: authHeaders(header) });
export const postReply = (id: string, parentId: number, content: string, header: string) =>
  api.post<VideoComment>(base(id) + '/' + parentId + '/replies', { content }, { headers: authHeaders(header) });
export const getLikes = (id: string, commentId: number) =>
  api.get<{ likes: number }>(base(id) + '/' + commentId + '/likes');
export const toggleLike = (id: string, commentId: number, header: string) =>
  api.post<{ likes: number; liked: boolean }>(base(id) + '/' + commentId + '/likes',
    {}, { headers: authHeaders(header) });
