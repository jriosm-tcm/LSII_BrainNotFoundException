import { useEffect, useState } from 'react';
import axios, { AxiosError } from 'axios';
import { getEnv } from './utils/Env';

export type Video = { id: string; title: string; description: string; channel: string; videoUrl: string; thumbnailUrl: string };

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

const ALL_VIDEOS_URL = `${getEnv().API_BASE_URL}/videos`;
export function useAllVideos(query: string = '') {
  const [value, setValue] = useState<Video[]>([]);
  const [message, setMessage] = useState<string>('Loading...');
  const [loading, setLoading] = useState<LoadingState>('idle');

  useEffect(() => {
    const controller = new AbortController();
    const timeout = window.setTimeout(async () => {
      try {
        setLoading('loading');
        const term = query.trim();
        const url = term ? `${ALL_VIDEOS_URL}/search` : ALL_VIDEOS_URL;
        const response = await axios.get<Video[]>(url, {
          params: term ? { q: term } : undefined,
          signal: controller.signal,
        });
        setValue(response.data);
        setLoading('success');
      } catch (error: unknown) {
        if (axios.isCancel(error)) return;
        setLoading('error');
        setMessage('Error fetching videos: ' + (error as AxiosError).message);
      }
    }, query.trim() ? 250 : 0);

    return () => {
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, [query]);

  return { value, message, loading };
}
