import { useEffect, useState } from 'react';
import axios, { AxiosError } from 'axios';
import { getVideos } from './api/videos';
import type { Video } from './types';

export type { Video } from './types';

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

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
        const response = await getVideos(term, controller.signal);
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
