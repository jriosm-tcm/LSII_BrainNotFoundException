import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import type { AuthSession } from '../api/auth';
import { uploadVideo } from '../api/videos';

type UploadVideoPageProps = {
  session: AuthSession;
};

type ApiProblem = {
  detail?: string;
};

const MAX_VIDEO_BYTES = 100 * 1024 * 1024;

function UploadVideoPage({ session }: UploadVideoPageProps) {
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (submitting) {
      return;
    }

    setError('');

    const normalizedTitle = title.trim();

    if (!normalizedTitle || normalizedTitle.length > 200) {
      setError('El título es obligatorio y admite hasta 200 caracteres.');
      return;
    }

    if (description.length > 5000) {
      setError('La descripción admite hasta 5000 caracteres.');
      return;
    }

    if (!file) {
      setError('Selecciona un archivo MP4.');
      return;
    }

    if (file.size === 0 || file.size > MAX_VIDEO_BYTES) {
      setError('Selecciona un MP4 de hasta 100 MB.');
      return;
    }

    setSubmitting(true);

    try {
      await uploadVideo(
        {
          title: normalizedTitle,
          description: description.trim(),
          file,
        },
        session.authorization
      );

      navigate('/my-videos', { replace: true });
    } catch (error) {
      if (axios.isAxiosError<ApiProblem>(error)) {
        if (error.response?.status === 401) {
          setError('No se ha podido autenticar la petición. Vuelve a iniciar sesión.');
        } else if (error.response?.status === 413) {
          setError('La petición supera el tamaño permitido por el servidor.');
        } else {
          setError(error.response?.data?.detail || 'No se ha podido subir el vídeo. Inténtalo de nuevo.');
        }
      } else {
        setError('No se ha podido subir el vídeo. Inténtalo de nuevo.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="container py-5" style={{ maxWidth: 720 }}>
      <h1 className="mb-4">Subir vídeo</h1>

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="video-title" className="form-label">
            Título
          </label>

          <input
            id="video-title"
            className="form-control"
            type="text"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            maxLength={200}
            disabled={submitting}
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="video-description" className="form-label">
            Descripción
          </label>

          <textarea
            id="video-description"
            className="form-control"
            rows={5}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            maxLength={5000}
            disabled={submitting}
            aria-describedby="video-description-help"
          />

          <p id="video-description-help" className="form-text">
            {description.length}/5000 caracteres
          </p>
        </div>

        <div className="mb-3">
          <label htmlFor="video-file" className="form-label">
            Archivo de vídeo
          </label>

          <input
            id="video-file"
            className="form-control"
            type="file"
            accept=".mp4,video/mp4"
            onChange={(event) => setFile(event.currentTarget.files?.[0] ?? null)}
            disabled={submitting}
            aria-describedby="video-file-help"
            required
          />

          <p id="video-file-help" className="form-text">
            Archivo MP4 de hasta 100 MB.
          </p>
        </div>

        {error && (
          <p className="alert alert-danger" role="alert">
            {error}
          </p>
        )}

        <button className="btn btn-primary" type="submit" disabled={submitting || !file}>
          {submitting ? 'Subiendo…' : 'Publicar vídeo'}
        </button>
      </form>

      <Link to="/my-videos" className="d-inline-block mt-3">
        Ir a mis vídeos
      </Link>
    </main>
  );
}

export default UploadVideoPage;
