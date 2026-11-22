import { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import type { AuthSession } from '../api/auth';
import { getMyVideos } from '../api/videos';
import type { Video } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import EditVideoForm from '../components/EditVideoForm';
import '../styles/video-catalog.css';
import './MyVideosPage.css';

type MyVideosPageProps = {
  session: AuthSession;
};

type ApiProblem = {
  detail?: string;
};

function MyVideosPage({ session }: MyVideosPageProps) {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [editingVideoId, setEditingVideoId] = useState<number | null>(null);

  useEffect(() => {
    const controller = new AbortController();

    async function loadVideos() {
      setLoading(true);
      setError('');
      setMessage('');
      setEditingVideoId(null);

      try {
        const loadedVideos = await getMyVideos(session.authorization, controller.signal);

        setVideos(loadedVideos);
      } catch (error) {
        if (axios.isCancel(error)) {
          return;
        }

        if (axios.isAxiosError<ApiProblem>(error)) {
          if (error.response?.status === 401) {
            setError('No se ha podido autenticar la petición. Vuelve a iniciar sesión.');
          } else {
            setError(error.response?.data?.detail || 'No se han podido cargar tus vídeos.');
          }
        } else {
          setError('No se han podido cargar tus vídeos.');
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    void loadVideos();

    return () => {
      controller.abort();
    };
  }, [session.authorization]);

  function handleVideoSaved(updatedVideo: Video) {
    setVideos((current) => current.map((video) => (video.id === updatedVideo.id ? updatedVideo : video)));

    setEditingVideoId(null);
    setMessage('Los datos del vídeo se han guardado correctamente.');
  }

  return (
    <main className="my-videos-page">
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
        <h1 className="mb-0">Mis vídeos</h1>

        <Link to="/upload" className="btn btn-primary">
          Subir vídeo
        </Link>
      </div>

      {loading && <p role="status">Cargando tus vídeos…</p>}

      {error && (
        <p className="alert alert-danger" role="alert">
          {error}
        </p>
      )}

      {message && (
        <p className="alert alert-success" role="status">
          {message}
        </p>
      )}

      {!loading && !error && videos.length === 0 && <p className="empty-state">Todavía no has subido ningún vídeo.</p>}

      {!loading && !error && (
        <div className="video-catalog">
          {videos.map((video) => (
            <article key={video.id} className="video-item">
              <Link to={`/video/${video.id}`} className="text-decoration-none">
                {video.thumbnailUrl ? (
                  <img
                    src={`${getEnv().MEDIA_BASE_URL}${video.thumbnailUrl}`}
                    alt={video.title}
                    className="video-thumbnail"
                    loading="lazy"
                  />
                ) : (
                  <div className="video-placeholder" aria-hidden="true">
                    ▶
                  </div>
                )}

                <p className="video-title">{video.title}</p>
              </Link>

              {editingVideoId === video.id ? (
                <EditVideoForm
                  video={video}
                  authorization={session.authorization}
                  onSaved={handleVideoSaved}
                  onCancel={() => setEditingVideoId(null)}
                />
              ) : (
                <button
                  type="button"
                  className="btn btn-outline-primary mt-2"
                  disabled={editingVideoId !== null}
                  onClick={() => {
                    setMessage('');
                    setEditingVideoId(video.id);
                  }}
                >
                  Editar datos
                </button>
              )}
            </article>
          ))}
        </div>
      )}

      <Link to="/videos" className="d-inline-block mt-3">
        Volver al catálogo
      </Link>
    </main>
  );
}

export default MyVideosPage;
