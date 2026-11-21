import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { createReply } from '../api/comments';
import type { VideoComment } from '../api/comments';

type ReplyCommentFormProps = {
  comment: VideoComment;
  authorization: string;
  onCreated: (reply: VideoComment) => void;
  onCancel: () => void;
};

type ApiProblem = {
  detail?: string;
  errors?: {
    field: string;
    message: string;
  }[];
};

function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiProblem>(error)) {
    if (error.response?.status === 401) {
      return 'No se ha podido autenticar la petición. Vuelve a iniciar sesión.';
    }

    if (error.response?.status === 404) {
      return 'El comentario o el vídeo ya no están disponibles.';
    }

    const problem = error.response?.data;

    return (
      problem?.errors?.map((fieldError) => fieldError.message).join(' ') ||
      problem?.detail ||
      'No se ha podido publicar la respuesta.'
    );
  }

  return 'No se ha podido publicar la respuesta.';
}

function ReplyCommentForm({ comment, authorization, onCreated, onCancel }: ReplyCommentFormProps) {
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const contentInputId = `reply-content-${comment.id}`;
  const contentHelpId = `reply-content-help-${comment.id}`;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (submitting) {
      return;
    }

    setError('');

    const normalizedContent = content.trim();

    if (!normalizedContent) {
      setError('La respuesta no puede estar vacía.');
      return;
    }

    if (normalizedContent.length > 2000) {
      setError('La respuesta admite hasta 2000 caracteres.');
      return;
    }

    setSubmitting(true);

    try {
      const createdReply = await createReply(comment.videoId, comment.id, normalizedContent, authorization);

      onCreated(createdReply);
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-3 border-top border-secondary pt-3">
      <div className="mb-3">
        <label htmlFor={contentInputId} className="form-label">
          Responder a {comment.authorName}
        </label>

        <textarea
          id={contentInputId}
          className="form-control"
          rows={3}
          value={content}
          onChange={(event) => setContent(event.target.value)}
          maxLength={2000}
          disabled={submitting}
          aria-describedby={contentHelpId}
          required
        />

        <p id={contentHelpId} className="form-text text-light">
          {content.length}/2000 caracteres
        </p>
      </div>

      {error && (
        <p className="alert alert-danger" role="alert">
          {error}
        </p>
      )}

      <div className="d-flex flex-wrap gap-2">
        <button type="submit" className="btn btn-primary btn-sm" disabled={submitting || !content.trim()}>
          {submitting ? 'Publicando…' : 'Publicar respuesta'}
        </button>

        <button type="button" className="btn btn-outline-light btn-sm" onClick={onCancel} disabled={submitting}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

export default ReplyCommentForm;
