import { useState } from 'react';
import './App.css';
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

function App() {
  const [session, setSession] = useState<AuthSession | null>(null);

  function handleLogout() {
    setSession(null);
  }

  return (
    <Router>
      <Header email={session?.email ?? null} onLogout={handleLogout} />

      <Routes>
        <Route path="/" element={<VideosPage />} />
        <Route path="/videos" element={<VideosPage />} />

        <Route path="/video/:id" element={<VideoDetailPage session={session} />} />

        <Route path="/login" element={<LoginPage onLogin={setSession} />} />
        <Route path="/register" element={<RegisterPage onLogin={setSession} />} />

        <Route
          path="/profile"
          element={session ? <ProfilePage key={session.email} session={session} /> : <Navigate to="/login" replace />}
        />

        <Route
          path="/upload"
          element={
            session ? <UploadVideoPage key={session.email} session={session} /> : <Navigate to="/login" replace />
          }
        />

        <Route
          path="/my-videos"
          element={session ? <MyVideosPage key={session.email} session={session} /> : <Navigate to="/login" replace />}
        />
      </Routes>
    </Router>
  );
}

export default App;
