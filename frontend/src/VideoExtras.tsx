import { useEffect, useState } from 'react';
import axios from 'axios';
import { getEnv } from './utils/Env';
import type { Video } from './useAllVideos';

type Auth = { email: string; header: string };
type Comment = { id: number; author: string; content: string; createdAt: string; parentId: number | null };
const api = getEnv().API_BASE_URL;

export function VideoExtras({ video, auth, onUpdated }: { video: Video; auth: Auth | null; onUpdated: (video: Video) => void }) {
  const [comments, setComments] = useState<Comment[]>([]);
  const [content, setContent] = useState('');
  const [replyTo, setReplyTo] = useState<number | null>(null);
  const [replyText, setReplyText] = useState('');
  const [likes, setLikes] = useState<Record<number, number>>({});
  const [stats, setStats] = useState<{ views: number; comments: number } | null>(null);
  const [message, setMessage] = useState('');
  const [title, setTitle] = useState(video.title);
  const [description, setDescription] = useState(video.description);
  const [editing, setEditing] = useState(false);

  useEffect(() => {
    setTitle(video.title);
    setDescription(video.description);
  }, [video.id, video.title, video.description]);

  useEffect(() => {
    let active = true;
    axios.get<Comment[]>(api + '/videos/' + encodeURIComponent(video.id) + '/comments')
      .then(response => { if (active) setComments(response.data); })
      .catch(() => { if (active) setMessage('Could not load comments'); });
    axios.get<{views:number;comments:number}>(api + '/videos/' + encodeURIComponent(video.id) + '/stats')
      .then(response => { if (active) setStats(response.data); }).catch(() => {});
    return () => { active = false; };
  }, [video.id]);

  useEffect(() => {
    let active = true;
    Promise.all(comments.map(comment => axios.get<{likes:number}>(api + '/videos/' + video.id +
      '/comments/' + comment.id + '/likes').then(response => [comment.id, response.data.likes] as const)))
      .then(values => { if (active) setLikes(Object.fromEntries(values)); }).catch(() => {});
    return () => { active = false; };
  }, [video.id, comments]);

  const reply = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth || replyTo === null || !replyText.trim()) return;
    try {
      const response = await axios.post<Comment>(api + '/videos/' + video.id + '/comments/' + replyTo + '/replies',
        { content: replyText.trim() }, { headers: { Authorization: auth.header } });
      setComments(current => [...current, response.data]);
      setReplyText('');
      setReplyTo(null);
    } catch { setMessage('Could not publish reply'); }
  };

  const toggleLike = async (id: number) => {
    if (!auth) return;
    try {
      const response = await axios.post<{likes:number}>(api + '/videos/' + video.id +
        '/comments/' + id + '/likes', {}, { headers: { Authorization: auth.header } });
      setLikes(current => ({ ...current, [id]: response.data.likes }));
    } catch { setMessage('Could not update like'); }
  };

  const postComment = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth || !content.trim()) return;
    try {
      const response = await axios.post<Comment>(api + '/videos/' + encodeURIComponent(video.id) + '/comments',
        { content: content.trim() }, { headers: { Authorization: auth.header } });
      setComments(current => [...current, response.data]);
      setContent('');
      setMessage('');
    } catch { setMessage('Could not publish comment'); }
  };

  const saveMetadata = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth) return;
    try {
      await axios.put(api + '/videos/' + encodeURIComponent(video.id) + '/metadata',
        { title, description }, { headers: { Authorization: auth.header } });
      onUpdated({ ...video, title, description });
      setEditing(false);
      setMessage('Video information updated');
    } catch { setMessage('Could not update video information'); }
  };

  return <div className="video-extras">
    {auth && auth.email.toLowerCase() === video.channel.toLowerCase() && <>
      <button type="button" onClick={() => setEditing(!editing)}>Edit video details</button>
      {editing && <form onSubmit={saveMetadata}>
        <input aria-label="Edit video title" value={title} maxLength={200} required onChange={e => setTitle(e.target.value)} />
        <textarea aria-label="Edit video description" value={description} maxLength={5000} onChange={e => setDescription(e.target.value)} />
        <button type="submit">Save changes</button>
      </form>}
    </>}
    {stats && <p>{stats.views} views · {stats.comments} comments</p>}
    <h3>Comments ({comments.length})</h3>
    {comments.map(comment => <article key={comment.id} className="comment">
      <strong>{comment.author}</strong>
      <p>{comment.content}</p>
      {comment.parentId !== null && <small>Reply to comment #{comment.parentId}</small>}
      <div className="comment-actions">
        <button type="button" disabled={!auth} onClick={() => toggleLike(comment.id)}>Like ({likes[comment.id] ?? 0})</button>
        {auth && <button type="button" onClick={() => setReplyTo(comment.id)}>Reply</button>}
      </div>
    </article>)}
    {auth && replyTo !== null && <form onSubmit={reply}>
      <label>Reply to comment #{replyTo}</label>
      <textarea required maxLength={2000} value={replyText} onChange={e => setReplyText(e.target.value)} />
      <button type="submit">Send reply</button>
      <button type="button" onClick={() => setReplyTo(null)}>Cancel</button>
    </form>}}
    {auth ? <form onSubmit={postComment}>
      <textarea aria-label="Write a comment" placeholder="Write a comment..." required maxLength={2000}
        value={content} onChange={e => setContent(e.target.value)} />
      <button type="submit">Post comment</button>
    </form> : <p>Log in to comment.</p>}
    {message && <p role="status">{message}</p>}
  </div>;
}

export function ProfileEditor({ auth }: { auth: Auth }) {
  const [description, setDescription] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [message, setMessage] = useState('');
  useEffect(() => {
    axios.get<{ description: string; avatarUrl: string }>(api + '/profile/me',
      { headers: { Authorization: auth.header } })
      .then(response => { setDescription(response.data.description); setAvatarUrl(response.data.avatarUrl); })
      .catch(() => setMessage('Could not load profile'));
  }, [auth.header]);
  const save = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      await axios.put(api + '/profile/me', { description, avatarUrl },
        { headers: { Authorization: auth.header } });
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
