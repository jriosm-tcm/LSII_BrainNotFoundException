import { useEffect, useRef, useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import type { AuthSession } from '../api/auth';
import { getProfile, updateProfile, uploadAvatar } from '../api/profile';
import type { Profile } from '../api/profile';
import { getEnv } from '../utils/Env';

type ProfilePageProps = {
  session: AuthSession;
  onAvatarChange: (avatarUrl: string) => void;
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

    const problem = error.response?.data;

    return problem?.errors?.map((fieldError) => fieldError.message).join(' ') || problem?.detail || fallback;
  }

  return fallback;
}

function ProfilePage({ session, onAvatarChange }: ProfilePageProps) {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
  const avatarInputRef = useRef<HTMLInputElement>(null);
  const busy = saving || uploadingAvatar;

  useEffect(() => {
    const controller = new AbortController();

    async function loadProfile() {
      setLoading(true);
      setMessage('');
      setError('');

      try {
        const loadedProfile = await getProfile(session.authorization, controller.signal);

        setProfile(loadedProfile);
        setDescription(loadedProfile.description);
      } catch (error) {
        if (axios.isCancel(error)) {
          return;
        }

        setError(getErrorMessage(error, 'No se ha podido cargar el perfil.'));
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    void loadProfile();

    return () => {
      controller.abort();
    };
  }, [session.authorization]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (busy) {
      return;
    }

    setSaving(true);
    setMessage('');
    setError('');

    try {
      const updatedProfile = await updateProfile(description, session.authorization);

      setProfile(updatedProfile);
      setDescription(updatedProfile.description);
      setMessage('Perfil guardado correctamente.');
    } catch (error) {
      setError(getErrorMessage(error, 'No se ha podido guardar el perfil.'));
    } finally {
      setSaving(false);
    }
  }

  async function handleAvatarSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (busy) {
      return;
    }

    setMessage('');
    setError('');

    if (!avatarFile) {
      setError('Selecciona una imagen antes de subirla.');
      return;
    }

    if (avatarFile.size === 0 || avatarFile.size > 2 * 1024 * 1024) {
      setError('Selecciona una imagen de hasta 2 MB.');
      return;
    }

    setUploadingAvatar(true);

    try {
      const updatedProfile = await uploadAvatar(avatarFile, session.authorization);

      setProfile(updatedProfile);
      onAvatarChange(updatedProfile.avatarUrl);
      setAvatarFile(null);

      if (avatarInputRef.current) {
        avatarInputRef.current.value = '';
      }

      setMessage('Imagen de perfil actualizada correctamente.');
    } catch (error) {
      setError(getErrorMessage(error, 'No se ha podido subir la imagen de perfil.'));
    } finally {
      setUploadingAvatar(false);
    }
  }

  if (loading) {
    return (
      <main className="container py-5">
        <p role="status">Cargando perfil…</p>
      </main>
    );
  }

  if (!profile) {
    return (
      <main className="container py-5">
        <h1>Mi perfil</h1>

        <p className="alert alert-danger" role="alert">
          {error || 'No se ha podido cargar el perfil.'}
        </p>

        <Link to="/login">Ir a iniciar sesión</Link>
      </main>
    );
  }

  const mediaBaseUrl = new URL(getEnv().MEDIA_BASE_URL, window.location.origin);

  const avatarSrc = profile.avatarUrl ? new URL(profile.avatarUrl, mediaBaseUrl).href : '';

  return (
    <main className="container py-5" style={{ maxWidth: 640 }}>
      <h1 className="mb-4">Mi perfil</h1>

      <p>
        <strong>Nombre visible:</strong> {profile.username}
      </p>

      <p>
        <strong>Correo:</strong> {profile.email}
      </p>

      {message && (
        <p className="alert alert-success" role="status">
          {message}
        </p>
      )}

      {error && (
        <p className="alert alert-danger" role="alert">
          {error}
        </p>
      )}

      <form onSubmit={handleAvatarSubmit} className="mb-4">
        <h2 className="h5">Imagen de perfil</h2>

        <div className="mb-3">
          {avatarSrc ? (
            <img
              src={avatarSrc}
              alt={`Avatar de ${profile.username}`}
              width={120}
              height={120}
              className="rounded-circle border"
              style={{ objectFit: 'cover' }}
            />
          ) : (
            <p className="text-secondary">Todavía no tienes una imagen de perfil.</p>
          )}
        </div>

        <div className="mb-3">
          <label htmlFor="profile-avatar" className="form-label">
            Seleccionar imagen
          </label>

          <input
            ref={avatarInputRef}
            id="profile-avatar"
            className="form-control"
            type="file"
            accept="image/png,image/jpeg"
            onChange={(event) => setAvatarFile(event.currentTarget.files?.[0] ?? null)}
            disabled={busy}
            aria-describedby="profile-avatar-help"
          />

          <p id="profile-avatar-help" className="form-text">
            PNG o JPEG, hasta 2 MB y un máximo de 4096 píxeles por lado.
          </p>
        </div>

        <button className="btn btn-outline-primary" type="submit" disabled={busy || !avatarFile}>
          {uploadingAvatar ? 'Subiendo…' : 'Subir imagen'}
        </button>
      </form>

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="profile-description" className="form-label">
            Descripción
          </label>

          <textarea
            id="profile-description"
            className="form-control"
            rows={5}
            maxLength={500}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            disabled={busy}
            aria-describedby="profile-description-help"
          />

          <p id="profile-description-help" className="form-text">
            {description.length}/500 caracteres
          </p>
        </div>

        <button className="btn btn-primary" type="submit" disabled={busy}>
          {saving ? 'Guardando…' : 'Guardar cambios'}
        </button>
      </form>

      <Link to="/videos" className="d-inline-block mt-3">
        Volver a los vídeos
      </Link>
    </main>
  );
}

export default ProfilePage;
