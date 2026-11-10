import { useEffect, useState } from 'react';
import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import { Link, useParams } from 'react-router-dom';
import './VideoDetailPage.css';

const VideoDetailPage = () => {
  const { id } = useParams();
  const { loading, message, value } = useAllVideos();
  const [query, setQuery] = useState('');
  const [playbackError, setPlaybackError] = useState(false);


  useEffect(() => {
    document.title = 'ProTube - Detalle del Video';
  }, []);

  useEffect(() => {
    setPlaybackError(false);
  }, [id]);
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
    const video: any = value.find((video: any) => video.id === parseInt(id!));

    if (!video) {
      return <div>Video no encontrado</div>;
    }

    return (
      <div className="video-detail-page">
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
        </div>

        <div className="video-detail">
          <Link to="/videos" className="back-to-videos">
            ← Volver a vídeos
          </Link>
          <h1>{video.title}</h1>
          <video
            key={video.id}
            controls
            className="video-player"
            poster={`${getEnv().MEDIA_BASE_URL}${video.thumbnailUrl}`}
            src={`${getEnv().MEDIA_BASE_URL}${video.url}`}
            onError={() => setPlaybackError(true)}
          >
            Tu navegador no soporta la reproducción de vídeos.
          </video>

          {playbackError && (
            <p role="alert">
              No se ha podido reproducir el vídeo. Comprueba que el archivo esté disponible y tenga un formato
              compatible.
            </p>
          )}
          <p className="video-description">{video.description}</p>
        </div>
      </div>
    );
  }

  return <div>Idle...</div>;
};

export default VideoDetailPage;
