import { useEffect, useState } from 'react';
import axios, { AxiosError } from 'axios';
import { getEnv } from './utils/Env';

export type Video = {
  id: number;
  title: string;
  description: string | null;
  url: string;
  thumbnailUrl: string;
};

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

const ALL_VIDEOS_URL = `${getEnv().API_BASE_URL}/videos`;
export function useAllVideos(query = '') {
  const [value, setValue] = useState<Video[]>([]);
  const [message, setMessage] = useState<string>('Loading...');
  const [loading, setLoading] = useState<LoadingState>('idle');

  useEffect(() => {
    const term = query.trim();
    const controller = new AbortController();

    const timeout = window.setTimeout(
      async () => {
        setLoading('loading');

        try {
          const url = term ? `${ALL_VIDEOS_URL}/search` : ALL_VIDEOS_URL;
          const response = await axios.get<Video[]>(url, {
            params: term ? { q: term } : undefined,
            signal: controller.signal,
          });

          setValue(response.data);
          setLoading('success');
        } catch (error: unknown) {
          if (axios.isCancel(error)) {
            return;
          }

          setMessage('Error al cargar los vídeos: ' + (error as AxiosError).message);
          setLoading('error');
        }
      },
      term ? 250 : 0
    );

    return () => {
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, [query]);

  return { value, message, loading };
}
