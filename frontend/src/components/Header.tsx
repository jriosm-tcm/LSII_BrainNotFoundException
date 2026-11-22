import { useState } from 'react';
import { Link } from 'react-router-dom';
import { getEnv } from '../utils/Env';
import './Header.css';

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
    <header className="site-header">
      <Link to="/videos" className="header-brand" aria-label="ProTube: ir al catálogo">
        <span className="header-brand-symbol" aria-hidden="true">
          ▶
        </span>

        <span>
          Pro<span className="header-brand-accent">Tube</span>
        </span>
      </Link>

      <div className="header-actions">
        {email ? (
          <>
            {showAvatar ? (
              <img
                src={avatarSrc}
                alt={`Avatar de ${email}`}
                className="header-avatar"
                onError={() => setBrokenAvatar(avatarSrc)}
              />
            ) : (
              <span className="header-avatar-fallback" aria-hidden="true">
                {email.charAt(0).toUpperCase()}
              </span>
            )}

            <span className="header-email">{email}</span>

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
