import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import VideosPage from './pages/VideosPage';
import ProfilePage from './pages/ProfilePage';
import VideoDetailPage from './pages/VideoDetailPage';
import UploadVideoPage from './pages/UploadVideoPage';
import MyVideosPage from './pages/MyVideosPage';
import Header from './components/Header';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import type { AuthSession } from './api/auth';
import { getProfile } from './api/profile';

function App() {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [headerAvatar, setHeaderAvatar] = useState<string | null>(null);

  useEffect(() => {
    setHeaderAvatar(null);

    if (!session) {
      return;
    }

    const authorization = session.authorization;
    const controller = new AbortController();

    async function loadHeaderAvatar() {
      try {
        const profile = await getProfile(authorization, controller.signal);

        if (!controller.signal.aborted) {
          setHeaderAvatar((current) => current ?? profile.avatarUrl);
        }
      } catch {
        if (!controller.signal.aborted) {
          setHeaderAvatar((current) => current ?? '');
        }
      }
    }

    void loadHeaderAvatar();

    return () => {
      controller.abort();
    };
  }, [session]);

  function handleLogout() {
    setSession(null);
    setHeaderAvatar(null);
  }

  function handleAvatarChange(avatarUrl: string) {
    setHeaderAvatar(avatarUrl);
  }

  return (
    <Router>
      <div className="protube-app" data-bs-theme="dark">
        <Header
          key={session?.email ?? 'guest'}
          email={session?.email ?? null}
          avatarUrl={headerAvatar ?? ''}
          onLogout={handleLogout}
        />

        <Routes>
          <Route path="/" element={<VideosPage />} />
          <Route path="/videos" element={<VideosPage />} />

          <Route path="/video/:id" element={<VideoDetailPage session={session} />} />

          <Route path="/login" element={<LoginPage onLogin={setSession} />} />

          <Route path="/register" element={<RegisterPage onLogin={setSession} />} />

          <Route
            path="/profile"
            element={
              session ? (
                <ProfilePage key={session.email} session={session} onAvatarChange={handleAvatarChange} />
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />

          <Route
            path="/upload"
            element={
              session ? <UploadVideoPage key={session.email} session={session} /> : <Navigate to="/login" replace />
            }
          />

          <Route
            path="/my-videos"
            element={
              session ? <MyVideosPage key={session.email} session={session} /> : <Navigate to="/login" replace />
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
