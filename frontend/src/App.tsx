import { useState } from 'react';
import './App.css';
import { useAllVideos, type Video } from './useAllVideos';
import { getEnv } from './utils/Env';

const mediaUrl = (path: string) => path ? new URL(path, getEnv().API_BASE_URL).toString() : '';

function App() {
  const { loading, message, value } = useAllVideos();
  const [selected, setSelected] = useState<Video | null>(null);

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
      <video controls autoPlay poster={mediaUrl(selected.thumbnailUrl)} src={mediaUrl(selected.videoUrl)} />
      <h2>{selected.title}</h2>
      <p>{selected.channel}</p>
      <p>{selected.description}</p>
    </section>}
    {!selected && <section className="video-grid" aria-label="Available videos">
      {value.map(video => <button className="video-card" key={video.id} onClick={() => setSelected(video)}>
        {video.thumbnailUrl ? <img src={mediaUrl(video.thumbnailUrl)} alt="" /> : <div className="placeholder">▶</div>}
        <strong>{video.title}</strong>
        <span>{video.channel}</span>
      </button>)}
    </section>}
  </main>;
}

export default App;
