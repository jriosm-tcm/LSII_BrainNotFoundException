import { useEffect, useState } from 'react';
import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideosPage.css';

const VideosPage = () => {
  const [query, setQuery] = useState('');
  const { loading, message, value } = useAllVideos(query);
  const [brokenThumbnails, setBrokenThumbnails] = useState<number[]>([]);

  useEffect(() => {
    document.title = 'ProTube - Videos';
  }, []);

  return (
    <div className="videos-page">
      <div className="catalog-toolbar">
        <img src="/protube-logo-removebg-preview.png" alt="ProTube" className="search-logo" />
        <input
          className="search-field"
          type="search"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Buscar por título o descripción..."
          aria-label="Buscar vídeos"
        />

        {loading === 'success' && (
          <p>
            {value.length} {value.length === 1 ? 'vídeo' : 'vídeos'}
          </p>
        )}
      </div>

      {loading === 'loading' && <p role="status">Cargando vídeos…</p>}
      {loading === 'error' && <p role="alert">{message}</p>}

      {loading === 'success' && value.length === 0 && (
        <p className="empty-state">
          {query.trim()
            ? 'Ningún vídeo coincide con tu búsqueda. Prueba otro término.'
            : 'Todavía no hay vídeos disponibles.'}
        </p>
      )}

      <div className="video-grid">
        {value.map((video) => (
          <a key={video.id} className="video-item" href={`/video/${video.id}`}>
            {video.thumbnailUrl && !brokenThumbnails.includes(video.id) ? (
              <img
                src={`${getEnv().MEDIA_BASE_URL}${video.thumbnailUrl}`}
                alt={video.title}
                className="video-thumbnail"
                loading="lazy"
                onError={() =>
                  setBrokenThumbnails((current) => (current.includes(video.id) ? current : [...current, video.id]))
                }
              />
            ) : (
              <div className="video-placeholder" aria-hidden="true">
                ▶
              </div>
            )}
            <p className="video-title">{video.title}</p>
          </a>
        ))}
      </div>
    </div>
  );
};

export default VideosPage;
