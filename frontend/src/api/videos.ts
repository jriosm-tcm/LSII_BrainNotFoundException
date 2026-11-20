import axios from 'axios';
import { getEnv } from '../utils/Env';
import type { Video } from '../useAllVideos';

export type UploadVideoData = {
  title: string;
  description: string;
  file: File;
};

export async function uploadVideo(data: UploadVideoData, authorization: string): Promise<Video> {
  const formData = new FormData();

  formData.append('title', data.title);
  formData.append('description', data.description);
  formData.append('file', data.file);

  const response = await axios.post<Video>(`${getEnv().API_BASE_URL}/videos/upload`, formData, {
    headers: {
      Authorization: authorization,
    },
  });

  return response.data;
}

export async function getMyVideos(authorization: string, signal?: AbortSignal): Promise<Video[]> {
  const response = await axios.get<Video[]>(`${getEnv().API_BASE_URL}/videos/mine`, {
    headers: {
      Authorization: authorization,
    },
    signal,
  });

  return response.data;
}

export type UpdateVideoMetadataData = {
  title: string;
  description: string;
};

export async function updateVideoMetadata(
    id: number,
    data: UpdateVideoMetadataData,
    authorization: string
): Promise<Video> {
  const response = await axios.put<Video>(
      `${getEnv().API_BASE_URL}/videos/${id}/metadata`,
      data,
      {
        headers: {
          Authorization: authorization,
        },
      }
  );

  return response.data;
}
