import axios from 'axios';
import { getEnv } from '../utils/Env';

export type VideoComment = {
  id: number;
  videoId: number;
  parentId: number | null;
  authorId: string;
  authorName: string;
  content: string;
  createdAt: string;
};

export async function getComments(videoId: number, signal?: AbortSignal): Promise<VideoComment[]> {
  const response = await axios.get<VideoComment[]>(`${getEnv().API_BASE_URL}/videos/${videoId}/comments`, {
    signal,
  });

  return response.data;
}

export async function createComment(videoId: number, content: string, authorization: string): Promise<VideoComment> {
  const response = await axios.post<VideoComment>(
    `${getEnv().API_BASE_URL}/videos/${videoId}/comments`,
    {
      content,
    },
    {
      headers: {
        Authorization: authorization,
      },
    }
  );

  return response.data;
}

export async function createReply(
  videoId: number,
  commentId: number,
  content: string,
  authorization: string
): Promise<VideoComment> {
  const response = await axios.post<VideoComment>(
    `${getEnv().API_BASE_URL}/videos/${videoId}/comments/${commentId}/replies`,
    {
      content,
    },
    {
      headers: {
        Authorization: authorization,
      },
    }
  );

  return response.data;
}

export type CommentLikesResult = {
  likes: number;
};

export type CommentLikeResult = {
  likes: number;
  liked: boolean;
};

export async function getCommentLikes(
  videoId: number,
  commentId: number,
  signal?: AbortSignal
): Promise<CommentLikesResult> {
  const response = await axios.get<CommentLikesResult>(
    `${getEnv().API_BASE_URL}/videos/${videoId}/comments/${commentId}/likes`,
    {
      signal,
    }
  );

  return response.data;
}

export async function toggleCommentLike(
  videoId: number,
  commentId: number,
  authorization: string
): Promise<CommentLikeResult> {
  const response = await axios.post<CommentLikeResult>(
    `${getEnv().API_BASE_URL}/videos/${videoId}/comments/${commentId}/likes`,
    null,
    {
      headers: {
        Authorization: authorization,
      },
    }
  );

  return response.data;
}