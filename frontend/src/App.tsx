import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import VideosPage from './pages/VideosPage';
import VideoDetailPage from './pages/VideoDetailPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<VideosPage />} />
        <Route path="/videos" element={<VideosPage />} />
        <Route path="/video/:id" element={<VideoDetailPage />} />
      </Routes>
    </Router>
  );
}

export default App;
