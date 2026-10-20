import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import VideosPage from './pages/VideosPage';

function App() {
    return (
        <Router>
            <Routes>
                <Route
                    path="/"
                    element={
                        <>
                            <header className="App-header">
                                <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
                                <main>
                                    <button onClick={() => window.location.href = '/videos'}>Ir a Videos</button>
                                </main>
                            </header>
                        </>
                    }
                />
                <Route path="/videos" element={<VideosPage />} />
            </Routes>
        </Router>
    );
}

export default App;
