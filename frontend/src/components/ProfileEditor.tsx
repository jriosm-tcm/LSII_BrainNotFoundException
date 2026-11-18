import { useEffect, useState } from 'react';
import { getProfile, updateProfile, uploadAvatar } from '../api/profile';
import type { Auth } from '../types';
import { getEnv } from '../utils/Env';

export function ProfileEditor({ auth, onAvatarChange }: { auth: Auth; onAvatarChange?: (avatarUrl: string) => void }) {
  const [description, setDescription] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getProfile(auth.header).then(response => {
      setDescription(response.data.description);
      setAvatarUrl(response.data.avatarUrl);
    }).catch(() => setMessage('Could not load profile'));
  }, [auth.header]);

  const save = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    setMessage('');
    try {
      let nextAvatar = avatarUrl;
      if (file) {
        const uploaded = await uploadAvatar(file, auth.header);
        nextAvatar = uploaded.data.avatarUrl;
        setAvatarUrl(nextAvatar);
        setFile(null);
      }
      await updateProfile({ description, avatarUrl: nextAvatar }, auth.header);
      onAvatarChange?.(nextAvatar);
      setMessage('Profile saved successfully');
    } catch { setMessage('Could not save profile. Use a PNG or JPEG under 2 MB.'); }
    finally { setSaving(false); }
  };

  return <form className="profile-form" onSubmit={save}>
    <div className="avatar-edit">
      {avatarUrl ? <img src={avatarUrl.startsWith("/api/") ? getEnv().API_BASE_URL.replace(/\/api$/, "") + avatarUrl : avatarUrl} alt="Current avatar" /> : <span className="avatar-fallback">{auth.email[0].toUpperCase()}</span>}
      <div><strong>Profile picture</strong><p>Upload a PNG or JPEG image (max 2 MB)</p>
        <input type="file" aria-label="Choose profile picture" accept="image/png,image/jpeg"
          onChange={e => setFile(e.target.files?.[0] ?? null)} />
      </div>
    </div>
    <label htmlFor="profile-description">About you</label>
    <textarea id="profile-description" maxLength={500} placeholder="Tell others about yourself"
      value={description} onChange={e => setDescription(e.target.value)} />
    <button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save changes'}</button>
    {message && <p role="status">{message}</p>}
  </form>;
}
