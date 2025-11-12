require('dotenv').config();
const NodeMediaServer = require('node-media-server');
const axios = require('axios');
const path = require('path');
const fs = require('fs'); 
const http = require('http');
const { createProxyMiddleware } = require('http-proxy-middleware');

// --- Cấu hình NMS (Đã sửa mediaroot) ---
const config = {
  rtmp: {
    port: 1935,
    chunk_size: 60000,
    gop_cache: true,
    ping: 30,
    ping_timeout: 60,
  },
  http: {
    port: 8000, // Port NMS gốc
    allow_origin: '*',
    mediaroot: '/media', // Đường dẫn tuyệt đối trong Docker
  },
  trans: {
    ffmpeg: '/usr/bin/ffmpeg',
    tasks: [
      {
        app: 'live',
        hls: true,
        hlsFlags: '[hls_time=4:hls_list_size=10]',
      },
    ],
  },
};

// --- NMS Hooks (Giữ nguyên, đã đúng) ---
const nms = new NodeMediaServer(config);

nms.on('prePublish', async (id, StreamPath, args) => {
  const streamKey = StreamPath.split('/').pop();
  const session = nms.getSession(id);
  console.log(`[prePublish] Checking key: ${streamKey}`);
  try {
    const res = await axios.post(
      'http://host.docker.internal:8080/api/v1/streamers/validate',
      { streamKey },
      { headers: { 'X-INTERNAL-TOKEN': 'super-secret-stream-validation' } }
    );
    if (!res.data.success) {
      console.log('❌ Invalid stream key:', streamKey);
      session.reject();
      return;
    }
    const streamerName = res.data.data.streamerName;
    console.log(`✅ Stream key valid for streamer: ${streamerName}`);
    await axios.post(
      'http://host.docker.internal:8080/api/v1/streamers/start',
      { streamerName: streamerName, streamKey: streamKey },
      { headers: { 'X-INTERNAL-TOKEN': 'super-secret-stream-validation' } }
    );
    session.streamerName = streamerName;
  } catch (err) {
    console.error('Error in prePublish:', err.message);
    session.reject();
  }
});

nms.on('donePublish', async (id, StreamPath, args) => {
  const session = nms.getSession(id);
  const streamerName = session?.streamerName;
  if (!streamerName) return;
  try {
    await axios.post(
      'http://host.docker.internal:8080/api/v1/streamers/stop',
      { streamerName: streamerName },
      { headers: { 'X-INTERNAL-TOKEN': 'super-secret-stream-validation' } }
    );
    console.log(`[donePublish] Notified backend stream stopped for: ${streamerName}`);
  } catch (err) {
    console.error('Error in donePublish:', err.message);
  }
});

nms.run();
console.log('🚀 Node Media Server (RTMP/HLS) started on port 1935 / 8000');


// --- LOGIC REVERSE PROXY MỚI (SỬA DÙNG pathRewrite) ---

const apiLookupUrl = 'http://host.docker.internal:8080/api/v1/streamers/info';
const nmsStaticServer = 'http://localhost:8000'; // Target NMS gốc

const proxy = createProxyMiddleware({
  target: nmsStaticServer, // Target là server NMS (port 8000)
  changeOrigin: true,
  
  // SỬ DỤNG pathRewrite (cách này sẽ THAY THẾ đường dẫn)
  pathRewrite: async (path, req) => {
    // path ban đầu là: "/live/HaiLua/index.m3u8"
    const parts = path.split('/');

    // Chỉ xử lý các request HLS
    if (parts.length < 3 || parts[1] !== 'live') {
      return path; // Giữ nguyên đường dẫn (cho các request không phải HLS)
    }

    const streamerName = parts[2];
    const requestedFile = parts.slice(3).join('/'); // "index.m3u8" hoặc "segment123.ts"
    
    try {
      // 1. Hỏi Spring Boot: "HaiLua" là streamKey nào?
      const res = await axios.get(`${apiLookupUrl}/${streamerName}`);
      const realStreamKey = res.data.streamKey;
      
      // 2. "Dịch" URL
      const newPath = `/live/${realStreamKey}/${requestedFile}`;
      console.log(`[Proxy] Rewriting ${path} -> ${newPath}`);
      
      // 3. Trả về đường dẫn MỚI
      return newPath;

    } catch (e) {
      // Lỗi (stream offline hoặc không tìm thấy)
      console.error(`[Proxy] Stream offline or lookup failed for: ${streamerName}`);
      throw new Error('Stream offline'); 
    }
  },
  
  onError: (err, req, res) => {
    // Hàm này sẽ bắt lỗi từ pathRewrite
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('Stream not found or offline.');
  }
});

// Tạo server proxy (Giữ nguyên)
const proxyServer = http.createServer(proxy);
proxyServer.listen(8001); 
console.log('🚀 HLS Reverse Proxy (Public) started on port 8001');