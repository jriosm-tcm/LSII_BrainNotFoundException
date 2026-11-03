import { useEffect, useMemo, useState } from 'react';
import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideosPage.css';

const VideosPage = () => {
  const { loading, message, value } = useAllVideos();
  const [query, setQuery] = useState('');
  const [brokenThumbnails, setBrokenThumbnails] = useState<number[]>([]);

  useEffect(() => {
    document.title = 'ProTube - Videos';
  }, []);

  const filteredVideos = useMemo(() => {
    const search = query.trim().toLocaleLowerCase();

    if (!search) {
      return value;
    }

    return value
      .map((video) => {
        const title = video.title.toLocaleLowerCase();
        const description = (video.description ?? '').toLocaleLowerCase();

        const score =
          title === search
            ? 4
            : title.startsWith(search)
              ? 3
              : title.includes(search)
                ? 2
                : description.includes(search)
                  ? 1
                  : 0;

        return { video, score };
      })
      .filter((item) => item.score > 0)
      .sort((a, b) => b.score - a.score || a.video.title.localeCompare(b.video.title))
      .map((item) => item.video);
  }, [query, value]);

  if (loading === 'loading') {
    return <div>Loading...</div>;
  }

  if (loading === 'error') {
    return (
      <div>
        <h3>Error</h3>
        <p>{message}</p>
      </div>
    );
  }

  if (loading === 'success') {
    return (
      <div>
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

          <p>
            {filteredVideos.length} {filteredVideos.length === 1 ? 'vídeo' : 'vídeos'}
          </p>
        </div>

        {value.length === 0 && <p className="empty-state">Todavía no hay vídeos disponibles.</p>}

        {value.length > 0 && filteredVideos.length === 0 && (
          <p className="empty-state">Ningún vídeo coincide con tu búsqueda. Prueba otro término.</p>
        )}

        <div className="video-grid">
          {filteredVideos.map((video) => (
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
  }

  return <div>Preparando el catálogo...</div>;
};

export default VideosPage;
