import { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../api/profile';
import type { Auth } from '../types';

export function ProfileEditor({ auth }: { auth: Auth }) {
  const [description, setDescription] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [message, setMessage] = useState('');
  useEffect(() => {
    getProfile(auth.header)
      .then(response => { setDescription(response.data.description); setAvatarUrl(response.data.avatarUrl); })
      .catch(() => setMessage('Could not load profile'));
  }, [auth.header]);
  const save = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      await updateProfile({ description, avatarUrl }, auth.header);
      setMessage('Profile saved');
    } catch { setMessage('Could not save profile'); }
  };
  return <form onSubmit={save}>
    <h2>Your profile</h2>
    <textarea aria-label="Profile description" maxLength={500} placeholder="About you"
      value={description} onChange={e => setDescription(e.target.value)} />
    <input aria-label="Avatar URL" type="url" maxLength={500} placeholder="Avatar image URL"
      value={avatarUrl} onChange={e => setAvatarUrl(e.target.value)} />
    <button type="submit">Save profile</button>
    {message && <p role="status">{message}</p>}
  </form>;
}
