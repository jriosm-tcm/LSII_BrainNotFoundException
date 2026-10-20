import './App.css';
import { useAllVideos } from './useAllVideos';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import VideosPage from './pages/VideosPage';

function App() {
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
        </header>
        <Routes>
          <Route path="/videos" element={<VideosPage />} />
        </Routes>
      </div>
    </Router>
  );
}

function ContentApp() {
  const { loading, message, value } = useAllVideos();
  switch (loading) {
    case 'loading':
      return <div>Loading...</div>;
    case 'error':
      return (
        <div>
          <h3>Error</h3> <p>{message}</p>
        </div>
      );
    case 'success':
      return (
        <>
          <strong>Videos availables:</strong>
          <ul>
            {value.map((item: any) => (
              <li>
                {item.title} - {item.thumbnailUrl}
              </li>
            ))}
          </ul>
        </>
      );
  }
  return <div>Idle...</div>;
}

export default App;
