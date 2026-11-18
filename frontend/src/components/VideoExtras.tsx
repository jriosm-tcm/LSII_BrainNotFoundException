import { useEffect, useState } from 'react';
import { getComments, postComment, postReply, getLikes, toggleLike } from '../api/comments';
import { getVideoStats, editVideo } from '../api/videos';
import type { Auth, Video, VideoComment as Comment, VideoStats } from '../types';

export function VideoExtras({ video, auth, onUpdated }: { video: Video; auth: Auth | null; onUpdated: (video: Video) => void }) {
  const [comments, setComments] = useState<Comment[]>([]);
  const [content, setContent] = useState('');
  const [replyTo, setReplyTo] = useState<number | null>(null);
  const [replyText, setReplyText] = useState('');
  const [likes, setLikes] = useState<Record<number, number>>({});
  const [stats, setStats] = useState<VideoStats | null>(null);
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
    getComments(video.id)
      .then(response => { if (active) setComments(response.data); })
      .catch(() => { if (active) setMessage('Could not load comments'); });
    getVideoStats(video.id)
      .then(response => { if (active) setStats(response.data); }).catch(() => {});
    return () => { active = false; };
  }, [video.id]);

  useEffect(() => {
    let active = true;
    Promise.all(comments.map(comment => getLikes(video.id, comment.id).then(response => [comment.id, response.data.likes] as const)))
      .then(values => { if (active) setLikes(Object.fromEntries(values)); }).catch(() => {});
    return () => { active = false; };
  }, [video.id, comments]);

  const reply = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth || replyTo === null || !replyText.trim()) return;
    try {
      const response = await postReply(video.id, replyTo, replyText.trim(), auth.header);
      setComments(current => [...current, response.data]);
      setReplyText('');
      setReplyTo(null);
    } catch { setMessage('Could not publish reply'); }
  };

  const toggleLike = async (id: number) => {
    if (!auth) return;
    try {
      const response = await toggleLike(video.id, id, auth.header);
      setLikes(current => ({ ...current, [id]: response.data.likes }));
    } catch { setMessage('Could not update like'); }
  };

  const postComment = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth || !content.trim()) return;
    try {
      const response = await postComment(video.id, content.trim(), auth.header);
      setComments(current => [...current, response.data]);
      setContent('');
      setMessage('');
    } catch { setMessage('Could not publish comment'); }
  };

  const saveMetadata = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth) return;
    try {
      await editVideo(video.id, title, description, auth.header);
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
    </form>}
    {auth ? <form onSubmit={postComment}>
      <textarea aria-label="Write a comment" placeholder="Write a comment..." required maxLength={2000}
        value={content} onChange={e => setContent(e.target.value)} />
      <button type="submit">Post comment</button>
    </form> : <p>Log in to comment.</p>}
    {message && <p role="status">{message}</p>}
  </div>;
}

