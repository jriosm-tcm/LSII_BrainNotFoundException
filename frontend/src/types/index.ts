export type Auth = { email: string; header: string };
export type Video = { id: string; title: string; description: string; channel: string; videoUrl: string; thumbnailUrl: string };
export type VideoComment = { id: number; author: string; content: string; createdAt: string; parentId: number | null };
export type VideoStats = { views: number; comments: number };
