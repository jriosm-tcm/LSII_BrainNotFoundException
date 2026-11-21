import { useEffect, useState } from 'react';
import axios from 'axios';
import { getCommentLikes, toggleCommentLike } from '../api/comments';

type CommentLikeButtonProps = {
  videoId: number;
  commentId: number;
  authorization: string | null;
};

type ApiProblem = {
  detail?: string;
};

function getErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ApiProblem>(error)) {
    if (error.response?.status === 401) {
      return 'Vuelve a iniciar sesión para modificar tu me gusta.';
    }

    if (error.response?.status === 404) {
      return 'El comentario ya no está disponible en este vídeo.';
    }

    return error.response?.data?.detail || fallback;
  }

  return fallback;
}

function CommentLikeButton({ videoId, commentId, authorization }: CommentLikeButtonProps) {
  const [likes, setLikes] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();

    async function loadLikes() {
      setLoading(true);
      setError('');

      try {
        const result = await getCommentLikes(videoId, commentId, controller.signal);

        setLikes(result.likes);
      } catch (error) {
        if (axios.isCancel(error)) {
          return;
        }

        setError(getErrorMessage(error, 'No se ha podido cargar el contador.'));
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    void loadLikes();

    return () => {
      controller.abort();
    };
  }, [videoId, commentId]);

  async function handleClick() {
    if (!authorization || loading || updating || likes === null) {
      return;
    }

    setUpdating(true);
    setError('');

    try {
      const result = await toggleCommentLike(videoId, commentId, authorization);

      setLikes(result.likes);
    } catch (error) {
      setError(getErrorMessage(error, 'No se ha podido modificar tu me gusta.'));
    } finally {
      setUpdating(false);
    }
  }

  return (
    <div className="d-flex flex-column align-items-start gap-1">
      <button
        type="button"
        className="btn btn-outline-light btn-sm"
        onClick={handleClick}
        disabled={!authorization || loading || updating || likes === null}
        title={authorization ? 'Pulsa para añadir o retirar tu me gusta.' : 'Inicia sesión para dar me gusta.'}
      >
        {loading ? 'Cargando me gusta…' : updating ? 'Actualizando…' : `Me gusta (${likes ?? '—'})`}
      </button>

      {error && (
        <p className="small text-warning mb-0" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}

export default CommentLikeButton;
