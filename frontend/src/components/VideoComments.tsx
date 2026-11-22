import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import type { AuthSession } from '../api/auth';
import { createComment, getComments } from '../api/comments';
import type { VideoComment } from '../api/comments';
import ReplyCommentForm from './ReplyCommentsForm';
import CommentLikeButton from './CommentLikeButton';
import './VideoComments.css';

type VideoCommentsProps = {
  videoId: number;
  session: AuthSession | null;
};

type ApiProblem = {
  detail?: string;
  errors?: {
    field: string;
    message: string;
  }[];
};

function getErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ApiProblem>(error)) {
    if (error.response?.status === 401) {
      return 'No se ha podido autenticar la petición. Vuelve a iniciar sesión.';
    }

    if (error.response?.status === 404) {
      return 'El vídeo ya no está disponible.';
    }

    const problem = error.response?.data;

    return problem?.errors?.map((fieldError) => fieldError.message).join(' ') || problem?.detail || fallback;
  }

  return fallback;
}

function formatCreatedAt(value: string): string {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString('es-ES', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function VideoComments({ videoId, session }: VideoCommentsProps) {
  const [comments, setComments] = useState<VideoComment[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState('');
  const [message, setMessage] = useState('');
  const [replyToId, setReplyToId] = useState<number | null>(null);

  const contentInputId = `comment-content-${videoId}`;
  const contentHelpId = `comment-content-help-${videoId}`;

  useEffect(() => {
    const controller = new AbortController();

    async function loadComments() {
      setLoading(true);
      setLoadError('');

      try {
        const loadedComments = await getComments(videoId, controller.signal);

        setComments(loadedComments);
      } catch (error) {
        if (axios.isCancel(error)) {
          return;
        }

        setLoadError(getErrorMessage(error, 'No se han podido cargar los comentarios.'));
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    void loadComments();

    return () => {
      controller.abort();
    };
  }, [videoId]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!session || loading || loadError || submitting || replyToId !== null) {
      return;
    }

    setSubmitError('');
    setMessage('');

    const normalizedContent = content.trim();

    if (!normalizedContent) {
      setSubmitError('El comentario no puede estar vacío.');
      return;
    }

    if (normalizedContent.length > 2000) {
      setSubmitError('El comentario admite hasta 2000 caracteres.');
      return;
    }

    setSubmitting(true);

    try {
      const createdComment = await createComment(videoId, normalizedContent, session.authorization);

      setComments((current) => [...current, createdComment]);
      setContent('');
      setMessage('Comentario publicado correctamente.');
    } catch (error) {
      setSubmitError(getErrorMessage(error, 'No se ha podido publicar el comentario.'));
    } finally {
      setSubmitting(false);
    }
  }
  function handleReplyCreated(createdReply: VideoComment) {
    setComments((current) => [...current, createdReply]);
    setReplyToId(null);
    setMessage('Respuesta publicada correctamente.');
  }

  return (
    <section className="video-comments" aria-label="Comentarios del vídeo">
      <h2 className="h4 mb-3">Comentarios {!loading && !loadError && `(${comments.length})`}</h2>

      {loading && <p role="status">Cargando comentarios…</p>}

      {loadError && (
        <p className="alert alert-danger" role="alert">
          {loadError}
        </p>
      )}

      {!loading && !loadError && (
        <>
          {comments.length === 0 && <p>Todavía no hay comentarios.</p>}

          {comments.map((comment) => (
            <article
              key={comment.id}
              id={`comment-${videoId}-${comment.id}`}
              className={`card comment-card mb-3 ${comment.parentId !== null ? 'ms-3' : ''}`}
            >
              <div className="card-body">
                {comment.parentId !== null && (
                  <p className="small mb-2">
                    En respuesta a{' '}
                    <a href={`#comment-${videoId}-${comment.parentId}`} className="link-info">
                      {comments.find((parent) => parent.id === comment.parentId)?.authorName ??
                        `comentario #${comment.parentId}`}
                    </a>
                  </p>
                )}

                <div className="d-flex justify-content-between flex-wrap gap-2 mb-2">
                  <strong>{comment.authorName}</strong>

                  <time dateTime={comment.createdAt} className="small opacity-75">
                    {formatCreatedAt(comment.createdAt)}
                  </time>
                </div>

                <p className="comment-content">{comment.content}</p>

                <div className="mt-3">
                  <CommentLikeButton
                    videoId={videoId}
                    commentId={comment.id}
                    authorization={session?.authorization ?? null}
                  />
                </div>

                {session && (
                  <div className="mt-3">
                    {replyToId === comment.id ? (
                      <ReplyCommentForm
                        comment={comment}
                        authorization={session.authorization}
                        onCreated={handleReplyCreated}
                        onCancel={() => setReplyToId(null)}
                      />
                    ) : (
                      <button
                        type="button"
                        className="btn btn-outline-light btn-sm"
                        disabled={submitting || replyToId !== null}
                        onClick={() => {
                          setMessage('');
                          setSubmitError('');
                          setReplyToId(comment.id);
                        }}
                      >
                        Responder
                      </button>
                    )}
                  </div>
                )}
              </div>
            </article>
          ))}

          {message && (
            <p className="alert alert-success" role="status">
              {message}
            </p>
          )}

          {session ? (
            <form onSubmit={handleSubmit} className="mt-4">
              <div className="mb-3">
                <label htmlFor={contentInputId} className="form-label">
                  Escribe un comentario
                </label>

                <textarea
                  id={contentInputId}
                  className="form-control"
                  rows={4}
                  value={content}
                  onChange={(event) => setContent(event.target.value)}
                  maxLength={2000}
                  disabled={submitting || replyToId !== null}
                  aria-describedby={contentHelpId}
                  required
                />

                <p id={contentHelpId} className="form-text text-light">
                  {content.length}/2000 caracteres
                </p>
              </div>

              {submitError && (
                <p className="alert alert-danger" role="alert">
                  {submitError}
                </p>
              )}

              <button
                type="submit"
                className="btn btn-primary"
                disabled={submitting || replyToId !== null || !content.trim()}
              >
                {submitting ? 'Publicando…' : 'Publicar comentario'}
              </button>
            </form>
          ) : (
            <p className="mt-4">
              <Link to="/login">Inicia sesión</Link> para publicar un comentario.
            </p>
          )}
        </>
      )}
    </section>
  );
}

export default VideoComments;
