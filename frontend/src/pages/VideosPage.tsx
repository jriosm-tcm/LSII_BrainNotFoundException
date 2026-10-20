import { useEffect } from 'react';
import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideosPage.css';

const VideosPage = () => {
  const { loading, message, value } = useAllVideos();

  useEffect(() => {
    document.title = 'ProTube - Videos';
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
    return (
      <div className="video-grid">
        {value.map((video: any) => (
          <a key={video.id} className="video-item" href={`/video/${video.id}`}>
            <img
              src={`${getEnv().MEDIA_BASE_URL}${video.thumbnailUrl}`}
              alt={video.title}
              className="video-thumbnail"
            />
            <p className="video-title">{video.title}</p>
          </a>
        ))}
      </div>
    );
  }

  return <div>Idle...</div>;
};

export default VideosPage;
