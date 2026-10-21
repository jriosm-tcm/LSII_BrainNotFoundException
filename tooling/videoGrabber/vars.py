"""Executable paths for videoGrabber.

Install yt-dlp and FFmpeg and add their executables to PATH.
Set YT_DLP_BIN, FFMPEG_BIN or FFPROBE_BIN to override paths locally.
"""
import os

YT_DLP_BIN = os.environ.get("YT_DLP_BIN", "yt-dlp")
FFMPEG_BIN = os.environ.get("FFMPEG_BIN", "ffmpeg")
FFPROBE_BIN = os.environ.get("FFPROBE_BIN", "ffprobe")
