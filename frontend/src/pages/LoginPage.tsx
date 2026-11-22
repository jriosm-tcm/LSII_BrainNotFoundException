import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import type { AuthSession } from '../api/auth';
import './LoginPage.css';

type LoginPageProps = {
  onLogin: (session: AuthSession) => void;
};

function LoginPage({ onLogin }: LoginPageProps) {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (submitting) {
      return;
    }

    setMessage('');
    setSubmitting(true);

    try {
      const session = await login(email.trim(), password);

      setPassword('');
      onLogin(session);
      navigate('/videos', { replace: true });
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        setMessage('Correo o contraseña incorrectos.');
      } else {
        setMessage('No se ha podido iniciar sesión. Inténtalo de nuevo.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="login-page ui-panel">
      <h1 className="mb-4">Iniciar sesión</h1>

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="login-email" className="form-label">
            Correo electrónico
          </label>

          <input
            id="login-email"
            className="form-control"
            type="email"
            autoComplete="username"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            disabled={submitting}
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="login-password" className="form-label">
            Contraseña
          </label>

          <input
            id="login-password"
            className="form-control"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            disabled={submitting}
            required
          />
        </div>

        {message && (
          <p className="alert alert-danger" role="alert">
            {message}
          </p>
        )}

        <button className="btn btn-primary w-100" type="submit" disabled={submitting}>
          {submitting ? 'Comprobando…' : 'Iniciar sesión'}
        </button>
      </form>

      <p className="mt-3">
        ¿No tienes cuenta? <Link to="/register">Crear una cuenta</Link>
      </p>
      <Link to="/videos" className="d-inline-block mt-3">
        Volver a los vídeos
      </Link>
    </main>
  );
}

export default LoginPage;
