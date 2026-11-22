import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { login, register } from '../api/auth';
import type { AuthSession } from '../api/auth';
import './RegisterPage.css';

type RegisterPageProps = {
  onLogin: (session: AuthSession) => void;
};

type RegistrationProblem = {
  detail?: string;
  errors?: {
    field: string;
    message: string;
  }[];
};

function RegisterPage({ onLogin }: RegisterPageProps) {
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [accountCreated, setAccountCreated] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (submitting || accountCreated) {
      return;
    }

    setMessage('');
    setSubmitting(true);

    let registrationCompleted = false;

    try {
      const createdUser = await register({
        username: username.trim(),
        email: email.trim(),
        password,
      });

      registrationCompleted = true;

      const session = await login(createdUser.email, password);

      setPassword('');
      onLogin(session);
      navigate('/videos', { replace: true });
    } catch (error) {
      if (registrationCompleted) {
        setAccountCreated(true);
        setPassword('');
        setMessage(
          'Tu cuenta se ha creado, pero no se ha podido iniciar sesión automáticamente. Utiliza el enlace de iniciar sesión.'
        );
      } else if (axios.isAxiosError<RegistrationProblem>(error)) {
        const problem = error.response?.data;
        const status = error.response?.status;

        if (status === 409) {
          setMessage(problem?.detail || 'Ya existe una cuenta con ese correo.');
        } else if (status === 400) {
          const validationMessages = problem?.errors?.map((fieldError) => fieldError.message).join(' ');

          setMessage(validationMessages || problem?.detail || 'Revisa los datos del formulario.');
        } else if (!error.response) {
          setMessage('No se ha podido conectar con el servidor.');
        } else {
          setMessage('No se ha podido crear la cuenta. Inténtalo de nuevo.');
        }
      } else {
        setMessage('Se ha producido un error inesperado.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="register-page ui-panel">
      <h1 className="mb-4">Crear una cuenta</h1>

      {message && (
        <p
          className={`alert ${accountCreated ? 'alert-info' : 'alert-danger'}`}
          role={accountCreated ? 'status' : 'alert'}
        >
          {message}
        </p>
      )}

      {!accountCreated && (
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="register-username" className="form-label">
              Nombre de usuario
            </label>

            <input
              id="register-username"
              name="username"
              className="form-control"
              type="text"
              autoComplete="nickname"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              maxLength={100}
              disabled={submitting}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="register-email" className="form-label">
              Correo electrónico
            </label>

            <input
              id="register-email"
              name="email"
              className="form-control"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              maxLength={254}
              disabled={submitting}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="register-password" className="form-label">
              Contraseña
            </label>

            <input
              id="register-password"
              name="password"
              className="form-control"
              type="password"
              autoComplete="new-password"
              aria-describedby="register-password-help"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              minLength={15}
              disabled={submitting}
              required
            />

            <p id="register-password-help" className="form-text">
              Utiliza al menos 15 caracteres.
            </p>
          </div>

          <button className="btn btn-primary w-100" type="submit" disabled={submitting}>
            {submitting ? 'Creando cuenta…' : 'Crear cuenta'}
          </button>
        </form>
      )}

      <div className="mt-3">
        <Link to="/login">Ir a iniciar sesión</Link>

        <Link to="/videos" className="ms-3">
          Volver a los vídeos
        </Link>
      </div>
    </main>
  );
}

export default RegisterPage;
