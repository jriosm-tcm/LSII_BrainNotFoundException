import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { updateVideoMetadata } from '../api/videos';
import type { Video } from '../useAllVideos';

type EditVideoFormProps = {
  video: Video;
  authorization: string;
  onSaved: (video: Video) => void;
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

    if (error.response?.status === 403) {
      return 'Solo el propietario puede modificar este vídeo.';
    }

    if (error.response?.status === 404) {
      return 'El vídeo ya no está disponible.';
    }

    const problem = error.response?.data;

    return (
      problem?.errors?.map((fieldError) => fieldError.message).join(' ') ||
      problem?.detail ||
      'No se han podido guardar los cambios.'
    );
  }

  return 'No se han podido guardar los cambios.';
}

function EditVideoForm({ video, authorization, onSaved, onCancel }: EditVideoFormProps) {
  const [title, setTitle] = useState(video.title);
  const [description, setDescription] = useState(video.description ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const titleInputId = `edit-video-title-${video.id}`;
  const descriptionInputId = `edit-video-description-${video.id}`;
  const descriptionHelpId = `edit-video-description-help-${video.id}`;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (saving) {
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

    setSaving(true);

    try {
      const updatedVideo = await updateVideoMetadata(
        video.id,
        {
          title: normalizedTitle,
          description: description.trim(),
        },
        authorization
      );

      onSaved(updatedVideo);
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-3 border-top pt-3">
      <h2 className="h6">Editar vídeo</h2>

      <div className="mb-3">
        <label htmlFor={titleInputId} className="form-label">
          Título
        </label>

        <input
          id={titleInputId}
          className="form-control"
          type="text"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          maxLength={200}
          disabled={saving}
          required
        />
      </div>

      <div className="mb-3">
        <label htmlFor={descriptionInputId} className="form-label">
          Descripción
        </label>

        <textarea
          id={descriptionInputId}
          className="form-control"
          rows={5}
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          maxLength={5000}
          disabled={saving}
          aria-describedby={descriptionHelpId}
        />

        <p id={descriptionHelpId} className="form-text">
          {description.length}/5000 caracteres
        </p>
      </div>

      {error && (
        <p className="alert alert-danger" role="alert">
          {error}
        </p>
      )}

      <div className="d-flex flex-wrap gap-2">
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Guardando…' : 'Guardar cambios'}
        </button>

        <button type="button" className="btn btn-outline-secondary" onClick={onCancel} disabled={saving}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

export default EditVideoForm;
