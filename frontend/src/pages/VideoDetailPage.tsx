import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideoDetailPage.css';

const VideoDetailPage = () => {
  const { id } = useParams();
  const { loading, message, value } = useAllVideos();

  useEffect(() => {
    document.title = 'ProTube - Detalle del Video';
  }, []);

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
      <div className="video-detail">
        <h1>{video.title}</h1>
        <video controls className="video-player">
          <source src={`${getEnv().MEDIA_BASE_URL}${video.url}`} type="video/mp4" />
          Tu navegador no soporta la reproducción de videos.
        </video>
        <p className="video-description">{video.description}</p>
      </div>
    );
  }

  return <div>Idle...</div>;
};

export default VideoDetailPage;
