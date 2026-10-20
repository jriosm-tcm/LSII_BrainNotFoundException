import { useAllVideos } from '../useAllVideos';
import { getEnv } from '../utils/Env';
import './VideosPage.css';

const VideosPage = () => {
  const { loading, message, value } = useAllVideos();

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
          <div key={video.id} className="video-item">
            <img
              src={`${getEnv().MEDIA_BASE_URL}${video.thumbnailUrl}`}
              alt={video.title}
              className="video-thumbnail"
            />
            <p className="video-title">{video.title}</p>
          </div>
        ))}
      </div>
    );
  }

  return <div>Idle...</div>;
};

export default VideosPage;
