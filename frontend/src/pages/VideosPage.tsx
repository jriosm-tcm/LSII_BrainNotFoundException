import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideosPage.css';
import '../styles/search-toolbar.css';
import '../styles/video-catalog.css';

const VideosPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const query = searchParams.get('q') ?? '';
  const currentSearch = searchParams.toString();

  const { loading, message, value } = useAllVideos(query);
  const [brokenThumbnails, setBrokenThumbnails] = useState<number[]>([]);

  const handleQueryChange = (nextQuery: string) => {
    const nextParams = new URLSearchParams(searchParams);

    if (nextQuery) {
      nextParams.set('q', nextQuery);
    } else {
      nextParams.delete('q');
    }

    setSearchParams(nextParams, { replace: true });
  };
  useEffect(() => {
    document.title = 'ProTube - Videos';
  }, []);

  return (
    <div className="videos-page">
      <div className="discover-heading">
        <span className="eyebrow">PROTUBE DISCOVER</span>

        <h1>
          Explora vídeos<span className="heading-dot">.</span>
        </h1>

        <p>Descubre lo que comparten los creadores.</p>
      </div>
      <div className="search-toolbar">
        <img src="/protube-logo-removebg-preview.png" alt="ProTube" className="search-logo" />
        <input
          className="search-field"
          type="search"
          value={query}
          onChange={(event) => handleQueryChange(event.target.value)}
          placeholder="Buscar por título, descripción o autor..."
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

      <div className="video-catalog">
        {value.map((video) => (
          <Link
            key={video.id}
            className="video-item"
            to={{
              pathname: `/video/${video.id}`,
              search: currentSearch ? `?${currentSearch}` : '',
            }}
          >
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

            <p className="small mb-0">
              {video.authorName ? `Subido por ${video.authorName}` : 'Vídeo del catálogo inicial'}
            </p>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default VideosPage;
