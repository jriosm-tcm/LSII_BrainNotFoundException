import { Link } from 'react-router-dom';

type HeaderProps = {
  email: string | null;
  onLogout: () => void;
};

function Header({ email, onLogout }: HeaderProps) {
  return (
    <header className="navbar navbar-dark bg-dark px-3 py-2">
      <Link to="/videos" className="navbar-brand">
        ProTube
      </Link>

      <div className="d-flex align-items-center gap-3">
        {email ? (
          <>
            <span className="text-light">{email}</span>

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
