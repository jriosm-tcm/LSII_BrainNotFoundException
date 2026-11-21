import { useState } from 'react';
import { Link } from 'react-router-dom';
import { getEnv } from '../utils/Env';

type HeaderProps = {
  email: string | null;
  avatarUrl: string;
  onLogout: () => void;
};

function Header({ email, avatarUrl, onLogout }: HeaderProps) {
  const [brokenAvatar, setBrokenAvatar] = useState<string | null>(null);

  const avatarSrc = avatarUrl ? new URL(avatarUrl, new URL(getEnv().MEDIA_BASE_URL, window.location.origin)).href : '';

  const showAvatar = avatarSrc !== '' && brokenAvatar !== avatarSrc;

  return (
    <header className="navbar navbar-dark bg-dark px-3 py-2">
      <Link to="/videos" className="navbar-brand">
        ProTube
      </Link>

      <div className="d-flex align-items-center flex-wrap gap-2">
        {email ? (
          <>
            {showAvatar ? (
              <img
                src={avatarSrc}
                alt={`Avatar de ${email}`}
                width={36}
                height={36}
                className="rounded-circle border"
                style={{ objectFit: 'cover' }}
                onError={() => setBrokenAvatar(avatarSrc)}
              />
            ) : (
              <span
                className="d-inline-flex align-items-center justify-content-center rounded-circle bg-secondary text-white flex-shrink-0"
                style={{ width: 36, height: 36 }}
                aria-hidden="true"
              >
                {email.charAt(0).toUpperCase()}
              </span>
            )}

            <span className="text-light">{email}</span>

            <Link to="/profile" className="btn btn-outline-light">
              Mi perfil
            </Link>

            <Link to="/my-videos" className="btn btn-outline-light">
              Mis vídeos
            </Link>

            <Link to="/upload" className="btn btn-outline-light">
              Subir vídeo
            </Link>

            <button type="button" className="btn btn-outline-light" onClick={onLogout}>
              Cerrar sesión
            </button>
          </>
        ) : (
          <Link to="/login" className="btn btn-outline-light">
            Iniciar sesión
          </Link>
        )}
      </div>
    </header>
  );
}

export default Header;
