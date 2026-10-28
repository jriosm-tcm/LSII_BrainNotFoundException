import { useMemo, useState } from 'react';
import './App.css';
import { useAllVideos, type Video } from './useAllVideos';
import { getEnv } from './utils/Env';

const mediaUrl = (path: string) => path ? new URL(path, getEnv().API_BASE_URL).toString() : '';

function App() {
  const { loading, message, value } = useAllVideos();
  const [selected, setSelected] = useState<Video | null>(null);
  const [query, setQuery] = useState('');
  const [playbackError, setPlaybackError] = useState(false);
  const [brokenThumbnails, setBrokenThumbnails] = useState<string[]>([]);

  // Sprint 3 (US-08): rank exact title matches before other title and description matches.
  const filteredVideos = useMemo(() => {
    const search = query.trim().toLocaleLowerCase();
    if (!search) return value;
    return value.map(video => {
      const title = video.title.toLocaleLowerCase();
      const description = video.description.toLocaleLowerCase();
      const channel = video.channel.toLocaleLowerCase();
      const score = title === search ? 4 : title.startsWith(search) ? 3 : title.includes(search) ? 2
        : description.includes(search) || channel.includes(search) ? 1 : 0;
      return { video, score };
    }).filter(item => item.score > 0)
      .sort((a, b) => b.score - a.score || a.video.title.localeCompare(b.video.title))
      .map(item => item.video);
  }, [query, value]);

  const openVideo = (video: Video) => {
    setPlaybackError(false);
    setSelected(video);
  };

  return <main className="App">
    <header className="site-header">
      <img src="/protube-logo-removebg-preview.png" alt="ProTube" />
      <h1>Discover videos</h1>
    </header>
    {loading === 'loading' && <p>Loading videos...</p>}
    {loading === 'error' && <p role="alert">{message}</p>}
    {loading === 'success' && value.length === 0 && <p>No videos found. Add MP4 and JSON files using videoGrabber.</p>}
    {selected && <section className="player-section">
      <button type="button" onClick={() => setSelected(null)}>← Back to videos</button>
      <video key={selected.id} controls autoPlay poster={mediaUrl(selected.thumbnailUrl)}
        src={mediaUrl(selected.videoUrl)} onError={() => setPlaybackError(true)} />
      {playbackError && <p role="alert">This video could not be played. Check the MP4 file and its codec.</p>}
      <h2>{selected.title}</h2>
      <p>{selected.channel}</p>
      <p>{selected.description}</p>
    </section>}
    {!selected && <>
      <div className="catalog-toolbar">
        <input className="search-field" type="search" value={query}
          onChange={event => setQuery(event.target.value)}
          placeholder="Search by title, description or channel..."
          aria-label="Search videos" />
        <p>{filteredVideos.length} {filteredVideos.length === 1 ? 'video' : 'videos'}</p>
      </div>
      {loading === 'success' && value.length > 0 && filteredVideos.length === 0 &&
        <p className="empty-state">No videos match your search. Try a different term.</p>}
      <section className="video-grid" aria-label="Available videos">
        {filteredVideos.map(video => <button type="button" className="video-card" key={video.id} onClick={() => openVideo(video)}>
          {video.thumbnailUrl && !brokenThumbnails.includes(video.id)
            ? <img src={mediaUrl(video.thumbnailUrl)} alt="" loading="lazy"
                onError={() => setBrokenThumbnails(current => current.includes(video.id) ? current : [...current, video.id])} />
            : <div className="placeholder" aria-hidden="true">▶</div>}
          <strong>{video.title}</strong>
          <span>{video.channel}</span>
        </button>)}
      </section>
    </>}
  </main>;
}

export default App;
