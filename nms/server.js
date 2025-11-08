require('dotenv').config();
const NodeMediaServer = require('node-media-server');
const axios = require('axios');
const fs = require('fs');
const path = require('path');

const isWindows = process.platform === 'win32';

const config = {
  rtmp: {
    port: 1935,
    chunk_size: 60000,
    gop_cache: true,
    ping: 30,
    ping_timeout: 60,
  },
  http: {
    port: 8000,
    allow_origin: '*',
    mediaroot: './media',
  },
  trans: {
    ffmpeg: '/usr/bin/ffmpeg',
    tasks: [
      {
        app: 'live',
        hls: true,
        hlsFlags: '[hls_time=4:hls_list_size=10]',
        // hlsKeep: true  // this will keep all file .ts
      },
    ],
  },
};


const nms = new NodeMediaServer(config);
const mediaRoot = path.join(__dirname, 'media', 'live');

// Ensure mediaRoot exists
if (!fs.existsSync(mediaRoot)) {
  fs.mkdirSync(mediaRoot, { recursive: true });
}

nms.on('prePublish', async (id, StreamPath, args) => {
  const streamKey = StreamPath.split('/').pop();
  const session = nms.getSession(id);
  console.log(`[prePublish] Checking key: ${streamKey}`);

  try {
    const res = await axios.post(
      'http://host.docker.internal:8080/api/v1/streamers/validate',
      { streamKey },
      {
        headers: {
          'X-INTERNAL-TOKEN': 'super-secret-stream-validation',
          'Content-Type': 'application/json',
        },
      }
    );

    if (!res.data.success) {
      console.log('❌ Invalid stream key:', streamKey);
      session.reject();
      return;
    }

    const streamerName = res.data.data.streamerName;
    console.log(`✅ Stream key valid for streamer: ${streamerName}`);
    session.streamerName = streamerName;

    const streamKeyPath = path.join(mediaRoot, streamKey);
    const aliasPath = path.join(mediaRoot, streamerName);

    // Remove old alias/copy if exists
    if (fs.existsSync(aliasPath)) {
      fs.rmSync(aliasPath, { recursive: true, force: true });
      console.log(`🧹 Removed old alias/copy for ${streamerName}`);
    }

    // Polling for HLS index file
    const interval = setInterval(() => {
      const hlsIndex = path.join(streamKeyPath, 'index.m3u8');
      if (fs.existsSync(hlsIndex)) {
        try {
          if (isWindows) {
            fs.cpSync(streamKeyPath, aliasPath, { recursive: true });
            console.log(`🎬 Copied /live/${streamerName} → ${streamKeyPath}`);
          } else {
            fs.symlinkSync(streamKeyPath, aliasPath, 'dir');
            console.log(`🎬 Linked /live/${streamerName} → ${streamKeyPath}`);
          }
          clearInterval(interval);
        } catch (err) {
          console.error('Failed to create alias:', err.message);
        }
      }
    }, 1000);

    // Stop polling after 2 minutes to avoid infinite loop
    setTimeout(() => clearInterval(interval), 120000);

  } catch (err) {
    console.error('Error validating key:', err.message);
    session.reject();
  }
});

nms.on('donePublish', (id, StreamPath, args) => {
  const session = nms.getSession(id);
  const streamerName = session?.streamerName;
  if (!streamerName) return;

  const aliasPath = path.join(mediaRoot, streamerName);
  if (fs.existsSync(aliasPath)) {
    fs.rmSync(aliasPath, { recursive: true, force: true });
    console.log(`🧹 Removed alias for ${streamerName}`);
  }
});

nms.run();
console.log('🚀 Node Media Server started');
