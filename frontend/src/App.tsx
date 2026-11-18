import { useState } from 'react';
import axios from 'axios';
import './App.css';
import { ProfileEditor } from './components/ProfileEditor';
import { VideoExtras } from './components/VideoExtras';
import { useAllVideos } from './useAllVideos';
import type { Video, Auth } from './types';
import { register, getCurrentUser } from './api/auth';
import { uploadVideo, trackVideoOpen } from './api/videos';
import { getEnv } from './utils/Env';

const mediaUrl = (path: string) => path ? new URL(path, getEnv().API_BASE_URL).toString() : '';

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
      <img src="/protube-logo-removebg-preview.png" alt="ProTube" />
      <h1>Discover videos</h1>
    </header>

    <section className="account-panel" aria-label="Account">
      {auth ? <>
        <p>Signed in as {auth.email}</p>
        <ProfileEditor auth={auth} />
        <button type="button" onClick={() => { setAuth(null); setUploadMessage(''); }}>Log out</button>
        <form onSubmit={upload}>
          <h2>Upload a video</h2>
          <input aria-label="Video title" placeholder="Video title" required maxLength={200}
            value={uploadTitle} onChange={e => setUploadTitle(e.target.value)} />
          <textarea aria-label="Video description" placeholder="Description" maxLength={5000}
            value={uploadDescription} onChange={e => setUploadDescription(e.target.value)} />
          <input aria-label="MP4 file" type="file" accept=".mp4,video/mp4" required
            onChange={e => setUploadFile(e.target.files?.[0] ?? null)} />
          <button type="submit" disabled={!uploadFile}>Upload MP4</button>
          {uploadMessage && <p role="status">{uploadMessage}</p>}
        </form>
      </> : <form onSubmit={authenticate}>
        <h2>{registerMode ? 'Create account' : 'Log in'}</h2>
        <input type="email" aria-label="Email" placeholder="Email" autoComplete="username"
          value={email} onChange={e => setEmail(e.target.value)} required />
        <input type="password" aria-label="Password" placeholder="Password" minLength={registerMode ? 8 : undefined}
          autoComplete={registerMode ? 'new-password' : 'current-password'}
          value={password} onChange={e => setPassword(e.target.value)} required />
        <button type="submit">{registerMode ? 'Register' : 'Log in'}</button>
        <button type="button" onClick={() => { setRegisterMode(!registerMode); setAuthMessage(''); }}>
          {registerMode ? 'Already registered? Log in' : 'Create an account'}
        </button>
        {authMessage && <p role="alert">{authMessage}</p>}
      </form>}
    </section>
    {loading === 'loading' && <p>Loading videos...</p>}
    {loading === 'error' && <p role="alert">{message}</p>}
    {loading === 'success' && value.length === 0 && !query.trim() && <p>No videos found. Add MP4 and JSON files using videoGrabber.</p>}
    {selected && <section className="player-section">
      <button type="button" onClick={() => setSelected(null)}>← Back to videos</button>
      <video key={selected.id} controls autoPlay poster={mediaUrl(selected.thumbnailUrl)}
        src={mediaUrl(selected.videoUrl)} onError={() => setPlaybackError(true)} />
      {playbackError && <p role="alert">This video could not be played. Check the MP4 file and its codec.</p>}
      <h2>{selected.title}</h2>
      <p>{selected.channel}</p>
      <p>{selected.description}</p>
      <VideoExtras video={selected} auth={auth} onUpdated={setSelected} />
    </section>}
    {!selected && <>
      <div className="catalog-toolbar">
        <input className="search-field" type="search" value={query}
          onChange={event => setQuery(event.target.value)}
          placeholder="Search by title, description or channel..."
          aria-label="Search videos" />
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
          <strong>{video.title}</strong>
          <span>{video.channel}</span>
        </button>)}
      </section>
    </>}
  </main>;
}

export default App;
