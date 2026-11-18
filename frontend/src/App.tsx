import { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';
import { ProfileEditor } from './components/ProfileEditor';
import { VideoExtras } from './components/VideoExtras';
import { useAllVideos } from './useAllVideos';
import type { Video, Auth } from './types';
import { register, getCurrentUser } from './api/auth';
import { uploadVideo, trackVideoOpen, getMyVideos } from './api/videos';
import { getEnv } from './utils/Env';

const mediaUrl = (path: string) => path ? new URL(path, getEnv().MEDIA_BASE_URL + '/').toString() : '';

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
  const [route, setRoute] = useState(window.location.pathname);
  const registerMode = route === '/register';
  const navigate = (path: string) => { window.history.pushState({}, '', path); setRoute(path); setAuthMessage('');  };
  useEffect(() => { const onPopState = () => setRoute(window.location.pathname); window.addEventListener('popstate', onPopState); return () => window.removeEventListener('popstate', onPopState); }, []);
  const [uploadTitle, setUploadTitle] = useState('');
  const [uploadDescription, setUploadDescription] = useState('');
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadMessage, setUploadMessage] = useState('');
  const [myVideos, setMyVideos] = useState<Video[]>([]);
  const [myVideosMessage, setMyVideosMessage] = useState('');
  useEffect(() => {
    if (route !== '/my-videos' || !auth) return;
    getMyVideos(auth.header).then(response => { setMyVideos(response.data); setMyVideosMessage(''); })
      .catch(() => setMyVideosMessage('Could not load your videos'));
  }, [route, auth]);

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
      navigate('/');
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
      setUploadMessage('Video uploaded successfully.');
      setUploadTitle('');
      setUploadDescription('');
      setUploadFile(null);
      navigate('/my-videos');
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

  if (route === '/login' || route === '/register') return <main className="auth-page">
    <header className="auth-topbar"><button type="button" className="brand" onClick={() => navigate('/')}>
      <span className="brand-symbol">▶</span><span>Pro<span className="brand-accent">Tube</span></span>
    </button><button type="button" className="back-button" onClick={() => navigate('/')}>← Back to videos</button></header>
    <section className="auth-card" aria-label={registerMode ? 'Registration' : 'Login'}>
      <span className="eyebrow">WELCOME TO PROTUBE</span>
      <h1>{registerMode ? 'Create your account' : 'Welcome back'}</h1>
      <p>{registerMode ? 'Join ProTube and start sharing your videos.' : 'Log in to your account to continue.'}</p>
      <form onSubmit={authenticate} className="auth-form">
        <label htmlFor="auth-email">Email address</label>
        <input id="auth-email" type="email" placeholder="you@example.com" autoComplete="username"
          value={email} onChange={e => setEmail(e.target.value)} required />
        <label htmlFor="auth-password">Password</label>
        <input id="auth-password" type="password" placeholder={registerMode ? 'At least 8 characters' : 'Your password'}
          minLength={registerMode ? 8 : undefined} autoComplete={registerMode ? 'new-password' : 'current-password'}
          value={password} onChange={e => setPassword(e.target.value)} required />
        {authMessage && <p role="alert" className="auth-error">{authMessage}</p>}
        <button type="submit" className="auth-submit">{registerMode ? 'Create account' : 'Log in'}</button>
      </form>
      <p className="auth-switch">{registerMode ? 'Already have an account?' : "Don't have an account?"} <button type="button" onClick={() => navigate(registerMode ? '/login' : '/register')}>{registerMode ? 'Log in' : 'Sign up'}</button></p>
    </section>
  </main>;

  return <main className="App">
    <header className="site-header">
      <button type="button" className="brand" onClick={() => { navigate('/'); setSelected(null); setQuery(''); }} aria-label="ProTube home">
        <span className="brand-symbol">▶</span><span>Pro<span className="brand-accent">Tube</span></span>
      </button>
      <div className="header-search"><span aria-hidden="true">⌕</span>
        <input className="search-field" type="search" value={query}
          onChange={event => { setQuery(event.target.value); setSelected(null); navigate('/'); }}
          placeholder="Search videos, creators..." aria-label="Search videos" />
      </div>
      <div className="header-actions">
        {auth && <span className="user-chip">{auth.email.slice(0, 1).toUpperCase()}</span>}
        <button type="button" className="account-toggle" onClick={() => navigate(auth ? '/profile' : '/login')}>
          {auth ? 'My account' : 'Log in / Sign up'} <span aria-hidden="true">⌄</span>
        </button>
      </div>
    </header>
    <div className="app-layout">
      <aside className="sidebar" aria-label="Navigation">
        <p className="sidebar-label">DISCOVER</p>
        <button className="nav-item active" type="button" onClick={() => { navigate('/'); setSelected(null); setQuery(''); }}>⌂ <span>Home</span></button>
        <button className="nav-item" type="button" onClick={() => { navigate('/'); setSelected(null); document.querySelector<HTMLInputElement>('.header-search input')?.focus(); }}>⌕ <span>Explore</span></button>
        <div className="sidebar-divider" />
        <p className="sidebar-label">YOUR SPACE</p>
        <button className="nav-item" type="button" onClick={() => navigate(auth ? '/profile' : '/login')}>◉ <span>My profile</span></button>
        <button className="nav-item" type="button" onClick={() => navigate(auth ? '/upload' : '/login')}>＋ <span>Upload video</span></button>
        <button className="nav-item" type="button" onClick={() => navigate(auth ? '/my-videos' : '/login')}>▣ <span>My videos</span></button>
        <div className="sidebar-footer">ProTube · Video platform</div>
      </aside>
      <div className="content-area">
        {route === '/profile' && auth && <section className="dashboard-page">
          <span className="eyebrow">YOUR SPACE</span><h1>My profile</h1>
          <p className="dashboard-subtitle">Manage your public profile and picture.</p>
          <div className="dashboard-card"><p className="signed-in">Signed in as <strong>{auth.email}</strong></p>
            <ProfileEditor auth={auth} />
            <button type="button" className="secondary-button" onClick={() => { setAuth(null); navigate('/'); }}>Log out</button>
          </div>
        </section>}
        {route === '/upload' && auth && <section className="dashboard-page">
          <span className="eyebrow">CREATOR STUDIO</span><h1>Upload video</h1>
          <p className="dashboard-subtitle">Share something new with your audience.</p>
          <div className="dashboard-card"><form onSubmit={upload} className="upload-form">
            <label htmlFor="video-title">Title</label>
            <input id="video-title" placeholder="Give your video a title" required maxLength={200}
              value={uploadTitle} onChange={e => setUploadTitle(e.target.value)} />
            <label htmlFor="video-description">Description</label>
            <textarea id="video-description" placeholder="What's your video about?" maxLength={5000}
              value={uploadDescription} onChange={e => setUploadDescription(e.target.value)} />
            <label htmlFor="video-file">Video file (MP4, up to 100 MB)</label>
            <input id="video-file" type="file" accept=".mp4,video/mp4" required
              onChange={e => setUploadFile(e.target.files?.[0] ?? null)} />
            <button type="submit" disabled={!uploadFile}>Publish video</button>
            {uploadMessage && <p role="status">{uploadMessage}</p>}
          </form></div>
        </section>}
        {route === '/my-videos' && auth && <section className="dashboard-page">
          <span className="eyebrow">CREATOR STUDIO</span><h1>My videos</h1>
          <p className="dashboard-subtitle">All the videos you've uploaded.</p>
          {myVideosMessage && <p role="alert">{myVideosMessage}</p>}
          {!myVideosMessage && myVideos.length === 0 && <p className="empty-state">You haven't uploaded any videos yet.</p>}
          <section className="video-grid">
            {myVideos.map(video => <button type="button" className="video-card" key={video.id}
              onClick={() => { setSelected(video); navigate('/'); }}>
              {video.thumbnailUrl ? <img src={mediaUrl(video.thumbnailUrl)} alt="" /> : <div className="placeholder">▶</div>}
              <div className="video-card-info"><span className="creator-avatar">{video.channel[0]?.toUpperCase()}</span>
                <div className="video-card-text"><strong>{video.title}</strong><span>{video.channel}</span></div></div>
            </button>)}
          </section>
          <button type="button" className="studio-link" onClick={() => navigate('/upload')}>＋ Upload another video</button>
        </section>}
        {route === '/' && <>
        {loading === 'loading' && <p className="loading-state">Loading videos...</p>}
        {loading === 'error' && <p role="alert">{message}</p>}
        {loading === 'success' && value.length === 0 && !query.trim() && <p className="empty-state">No videos yet. Upload your first video to get started.</p>}
        {selected ? <section className="player-section">
          <button type="button" className="back-button" onClick={() => setSelected(null)}>← Back to explore</button>
          <video key={selected.id} controls autoPlay poster={mediaUrl(selected.thumbnailUrl)}
            src={mediaUrl(selected.videoUrl)} onError={() => setPlaybackError(true)} />
          {playbackError && <p role="alert">This video could not be played. Check the MP4 file and its codec.</p>}
          <h1 className="watch-title">{selected.title}</h1>
          <p className="watch-creator"><span className="creator-avatar">{selected.channel.slice(0, 1).toUpperCase()}</span>{selected.channel}</p>
          <p className="watch-description">{selected.description}</p>
          <VideoExtras video={selected} auth={auth} onUpdated={setSelected} />
        </section> : <>
          <div className="discover-heading"><div><span className="eyebrow">PROTUBE DISCOVER</span><h1>Explore videos<span className="heading-dot">.</span></h1><p>Watch what creators are sharing.</p></div><span className="live-pill"><span className="live-dot"/> DISCOVER</span></div>
          <div className="catalog-toolbar"><span className="section-title">Latest uploads</span><p>{value.length} {value.length === 1 ? 'video' : 'videos'}</p></div>
          {loading === 'success' && query.trim() && value.length === 0 && <p className="empty-state">No videos match your search. Try a different term.</p>}
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
        </>}
      </div>
    </div>
  </main>;
}

export default App;
