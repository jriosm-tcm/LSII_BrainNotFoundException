import { useState } from 'react';
import axios from 'axios';
import './App.css';
import { ProfileEditor } from './components/ProfileEditor';
import { VideoExtras } from './components/VideoExtras';
import { useAllVideos } from './useAllVideos';
import type { Video, Auth } from './types';
import { register, getCurrentUser } from './api/auth';
import { uploadVideo, trackVideoOpen } from './api/videos';

const mediaUrl = (path: string) => path ? new URL(path, window.location.origin).toString() : '';

function App() {
  const [query, setQuery] = useState('');
  const { loading, message, value } = useAllVideos(query);
  const [selected, setSelected] = useState<Video | null>(null);
  const [playbackError, setPlaybackError] = useState(false);
  const [brokenThumbnails, setBrokenThumbnails] = useState<string[]>([]);


  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [auth, setAuth] = useState<Auth | null>(null);
  const [authMessage, setAuthMessage] = useState('');
  const [registerMode, setRegisterMode] = useState(false);
  const [uploadTitle, setUploadTitle] = useState('');
  const [uploadDescription, setUploadDescription] = useState('');
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadMessage, setUploadMessage] = useState('');
  const [accountOpen, setAccountOpen] = useState(false);

  const authenticate = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setAuthMessage('');
    const bytes = new TextEncoder().encode(email + ':' + password);
    const header = 'Basic ' + btoa(Array.from(bytes, b => String.fromCharCode(b)).join(''));
    try {
      if (registerMode) await register(email, password);
      const response = await getCurrentUser(header);
      setAuth({ email: response.data.email, header });
      setPassword('');
    } catch (error) {
      setAuthMessage(axios.isAxiosError(error)
        ? (error.response?.data?.error || (error.response?.status === 401 ? 'Invalid email or password' : 'Unable to authenticate'))
        : 'Unable to authenticate');
    }
  };

  const upload = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth || !uploadFile) return;
    setUploadMessage('Uploading...');
    const form = new FormData();
    form.append('file', uploadFile);
    form.append('title', uploadTitle);
    form.append('description', uploadDescription);
    try {
      await uploadVideo(form, auth.header);
      setUploadMessage('Video uploaded. Refresh the catalogue to see it.');
      setUploadTitle('');
      setUploadDescription('');
      setUploadFile(null);
    } catch (error) {
      setUploadMessage(axios.isAxiosError(error)
        ? (error.response?.data?.error || 'Upload failed') : 'Upload failed');
    }
  };

  const openVideo = (video: Video) => {
    void trackVideoOpen(video.id).catch(() => {});
    setPlaybackError(false);
    setSelected(video);
  };

  return <main className="App">
    <header className="site-header">
      <button type="button" className="brand" onClick={() => { setSelected(null); setQuery(''); }} aria-label="ProTube home">
        <span className="brand-symbol">▶</span><span>Pro<span className="brand-accent">Tube</span></span>
      </button>
      <div className="header-search">
        <span aria-hidden="true">⌕</span>
        <span className="section-title">Latest uploads</span>
        <p>{value.length} {value.length === 1 ? 'video' : 'videos'}</p>
      </div>
      {loading === 'success' && query.trim() && value.length === 0 &&
        <p className="empty-state">No videos match your search. Try a different term.</p>}
      <section className="video-grid" aria-label="Available videos">
        {value.map(video => <button type="button" className="video-card" key={video.id} onClick={() => openVideo(video)}>
          {video.thumbnailUrl && !brokenThumbnails.includes(video.id)
            ? <img src={mediaUrl(video.thumbnailUrl)} alt="" loading="lazy"
                onError={() => setBrokenThumbnails(current => current.includes(video.id) ? current : [...current, video.id])} />
            : <div className="placeholder" aria-hidden="true">▶</div>}
          <div className="video-card-info"><span className="creator-avatar">{video.channel.slice(0, 1).toUpperCase()}</span><div className="video-card-text"><strong>{video.title}</strong><span>{video.channel}</span></div></div>
        </button>)}
      </section>
    </>}
      </div>
    </div>
  </main>;
}

export default App;
