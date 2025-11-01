FROM node:19

# Cài ffmpeg để Node Media Server có thể transcode HLS
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN mkdir -p /media
VOLUME ["/media"]

EXPOSE 1935 8000

CMD ["node", "server.js"]
