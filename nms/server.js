const NodeMediaServer = require('node-media-server');
const axios = require('axios');

const config = {
  rtmp: {
    port: 1935,
    chunk_size: 60000,
    gop_cache: true,
    ping: 30,
    ping_timeout: 60
  },
  http: {
    port: 8000,
    allow_origin: '*',
    mediaroot: './media'
  },
  trans: {
    ffmpeg: '/usr/bin/ffmpeg',
    tasks: [
      {
        app: 'live',
        hls: true,
        hlsFlags: '[hls_time=2:hls_list_size=3:hls_flags=delete_segments]',
        mp4: false
      }
    ]
  }
};

const nms = new NodeMediaServer(config);

nms.on('prePublish', async (id, StreamPath, args) => {
  const streamKey = StreamPath.split('/').pop();
  console.log(`[prePublish] Checking key: ${streamKey}`);

  try {
    const res = await axios.post('http://localhost:8080/api/v1/streamers/validate', {
      streamKey: streamKey
    });

    if (!res.data.success) {
      console.log('Invalid stream key:', streamKey);
      const session = nms.getSession(id);
      session.reject();
    } else {
      console.log('Stream key valid for streamer:', res.data.data.streamerName);

      const session = nms.getSession(id);
      session.streamerName = res.data.data.streamerName;
    }
  } catch (err) {
    console.error('Error validating key:', err.message);
    const session = nms.getSession(id);
    session.reject();
  }
});

nms.run();
console.log('🚀 Node Media Server started');
