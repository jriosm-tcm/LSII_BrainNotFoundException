import { useEffect, useRef, useState } from 'react';
import axios from 'axios';
import { getVideoStats, registerVideoView } from '../api/videos';
import type { Video } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideoPlayer.css';

type VideoPlayerProps = {
  video: Video;
};

type ApiProblem = {
  detail?: string;
};

function getErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ApiProblem>(error)) {
    if (error.response?.status === 404) {
      return 'El vídeo ya no está disponible.';
    }

    return error.response?.data?.detail || fallback;
  }

  return fallback;
}

function VideoPlayer({ video }: VideoPlayerProps) {
  const [views, setViews] = useState<number | null>(null);
  const [loadError, setLoadError] = useState('');
  const [viewError, setViewError] = useState('');
  const [playbackError, setPlaybackError] = useState(false);

  const viewRequestedRef = useRef(false);

  useEffect(() => {
    const controller = new AbortController();

    async function loadStats() {
      setLoadError('');

      try {
        const stats = await getVideoStats(video.id, controller.signal);

        setViews((current) => Math.max(current ?? 0, stats.views));
      } catch (error) {
        if (axios.isCancel(error)) {
          return;
        }

        setLoadError(getErrorMessage(error, 'No se han podido cargar las estadísticas.'));
      }
    }

    void loadStats();

    return () => {
      controller.abort();
    };
  }, [video.id]);

  async function handlePlaying() {
    setPlaybackError(false);

    if (viewRequestedRef.current) {
      return;
    }

    viewRequestedRef.current = true;
    setViewError('');

    try {
      const stats = await registerVideoView(video.id);

      setViews((current) => Math.max(current ?? 0, stats.views));
    } catch (error) {
      setViewError(getErrorMessage(error, 'No se ha podido registrar la visualización.'));
    }
  }

  return (
    <div className="video-player-container">
      <video
        controls
        className="video-player"
        poster={video.thumbnailUrl ? `${getEnv().MEDIA_BASE_URL}${video.thumbnailUrl}` : undefined}
        src={`${getEnv().MEDIA_BASE_URL}${video.url}`}
        onPlaying={() => {
          void handlePlaying();
        }}
        onError={() => setPlaybackError(true)}
      >
        Tu navegador no soporta la reproducción de vídeos.
      </video>

      {playbackError && (
        <p role="alert">
          No se ha podido reproducir el vídeo. Comprueba que el archivo esté disponible y tenga un formato compatible.
        </p>
      )}

      {views !== null && (
        <p className="mb-3" aria-live="polite">
          {views} {views === 1 ? 'visualización' : 'visualizaciones'}
        </p>
      )}

      {views === null && !loadError && <p role="status">Cargando estadísticas…</p>}

      {views === null && loadError && (
        <p className="text-warning" role="alert">
          {loadError}
        </p>
      )}

      {viewError && (
        <p className="text-warning" role="alert">
          {viewError}
        </p>
      )}
    </div>
  );
}

export default VideoPlayer;
