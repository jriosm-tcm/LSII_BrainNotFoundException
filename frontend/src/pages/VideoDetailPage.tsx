import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { useAllVideos } from '../useAllVideos';
import type { AuthSession } from '../api/auth';
import VideoPlayer from '../components/VideoPlayer';
import VideoComments from '../components/VideoComments';
import './VideoDetailPage.css';

type VideoDetailPageProps = {
  session: AuthSession | null;
};

const VideoDetailPage = ({ session }: VideoDetailPageProps) => {
  const { id } = useParams();

  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [query, setQuery] = useState(searchParams.get('q') ?? '');

  const currentSearch = searchParams.toString();

  const backToVideos = currentSearch ? `/videos?${currentSearch}` : '/videos';

  const { loading, message, value } = useAllVideos();

  useEffect(() => {
    document.title = 'ProTube - Detalle del Video';
  }, []);

  const handleSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const term = query.trim();

    const nextParams = new URLSearchParams();

    if (term) {
      nextParams.set('q', term);
    }

    const search = nextParams.toString();

    navigate({
      pathname: '/videos',
      search: search ? `?${search}` : '',
    });
  };

  if (loading === 'loading' || loading === 'idle') {
    return <div>Cargando vídeo…</div>;
  }

  if (loading === 'error') {
    return (
      <div>
        <h3>Error</h3>
        <p>{message}</p>
      </div>
    );
  }

  const video = value.find((video) => video.id === Number(id));

  if (!video) {
    return <div>Vídeo no encontrado</div>;
  }

  return (
    <div className="video-detail-page">
      <form className="catalog-toolbar" onSubmit={handleSearch}>
        <img src="/protube-logo-removebg-preview.png" alt="ProTube" className="search-logo" />

        <input
          className="search-field"
          type="search"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Buscar por título, descripción o autor..."
          aria-label="Buscar vídeos"
        />

        <button type="submit" className="btn btn-primary">
          Buscar
        </button>
      </form>

      <div className="video-detail">
        <Link to={backToVideos} className="back-to-videos">
          ← Volver a vídeos
        </Link>

        <h1>{video.title}</h1>

        <p className="small mb-3">
          {video.authorName ? `Subido por ${video.authorName}` : 'Vídeo del catálogo inicial'}
        </p>

        <VideoPlayer key={video.id} video={video} />

        <p className="video-description">{video.description}</p>

        <VideoComments key={`${video.id}:${session?.email ?? 'guest'}`} videoId={video.id} session={session} />
      </div>
    </div>
  );
};

export default VideoDetailPage;
