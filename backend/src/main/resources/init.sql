-- Script para inicializar la base de datos

CREATE TABLE videos (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    url VARCHAR(2083) NOT NULL,
    thumbnail_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar los videos de la carpeta "videos"
INSERT INTO videos (title, description, url, thumbnail_url) VALUES
('Video 0', 'Descripción del Video 0', 'videos/0.mp4', 'videos/0.webp'),
('Video 1', 'Descripción del Video 1', 'videos/1.mp4', 'videos/1.webp'),
('Video 2', 'Descripción del Video 2', 'videos/2.mp4', 'videos/2.webp'),
('Video 3', 'Descripción del Video 3', 'videos/3.mp4', 'videos/3.webp'),
('Video 4', 'Descripción del Video 4', 'videos/4.mp4', 'videos/4.webp'),
('Video 5', 'Descripción del Video 5', 'videos/5.mp4', 'videos/5.webp'),
('Video 6', 'Descripción del Video 6', 'videos/6.mp4', 'videos/6.webp'),
('Video 7', 'Descripción del Video 7', 'videos/7.mp4', 'videos/7.webp'),
('Video 9', 'Descripción del Video 9', 'videos/9.mp4', 'videos/9.webp'),
('Video 10', 'Descripción del Video 10', 'videos/10.mp4', 'videos/10.webp'),
('Video 11', 'Descripción del Video 11', 'videos/11.mp4', 'videos/11.webp'),
('Video 12', 'Descripción del Video 12', 'videos/12.mp4', 'videos/12.webp'),
('Video 13', 'Descripción del Video 13', 'videos/13.mp4', 'videos/13.webp'),
('Video 14', 'Descripción del Video 14', 'videos/14.mp4', 'videos/14.webp'),
('Video 15', 'Descripción del Video 15', 'videos/15.mp4', 'videos/15.webp'),
('Video 16', 'Descripción del Video 16', 'videos/16.mp4', 'videos/16.webp'),
('Video 17', 'Descripción del Video 17', 'videos/17.mp4', 'videos/17.webp'),
('Video 18', 'Descripción del Video 18', 'videos/18.mp4', 'videos/18.webp'),
('Video 19', 'Descripción del Video 19', 'videos/19.mp4', 'videos/19.webp'),
('Video 20', 'Descripción del Video 20', 'videos/20.mp4', 'videos/20.webp'),
('Video 21', 'Descripción del Video 21', 'videos/21.mp4', 'videos/21.webp'),
('Video 22', 'Descripción del Video 22', 'videos/22.mp4', 'videos/22.webp'),
('Video 24', 'Descripción del Video 24', 'videos/24.mp4', 'videos/24.webp'),
('Video 25', 'Descripción del Video 25', 'videos/25.mp4', 'videos/25.webp'),
('Video 26', 'Descripción del Video 26', 'videos/26.mp4', 'videos/26.webp'),
('Video 27', 'Descripción del Video 27', 'videos/27.mp4', 'videos/27.webp'),
('Video 28', 'Descripción del Video 28', 'videos/28.mp4', 'videos/28.webp'),
('Video 29', 'Descripción del Video 29', 'videos/29.mp4', 'videos/29.webp'),
('Video 30', 'Descripción del Video 30', 'videos/30.mp4', 'videos/30.webp'),
('Video 31', 'Descripción del Video 31', 'videos/31.mp4', 'videos/31.webp'),
('Video 32', 'Descripción del Video 32', 'videos/32.mp4', 'videos/32.webp'),
('Video 33', 'Descripción del Video 33', 'videos/33.mp4', 'videos/33.webp'),
('Video 34', 'Descripción del Video 34', 'videos/34.mp4', 'videos/34.webp'),
('Video 35', 'Descripción del Video 35', 'videos/35.mp4', 'videos/35.webp'),
('Video 37', 'Descripción del Video 37', 'videos/37.mp4', 'videos/37.webp');
