import type { NextConfig } from "next";

const nextConfig = {
  // ESLint와 TypeScript 에러 무시 (배포용)
  eslint: {
    ignoreDuringBuilds: true,
  },
  typescript: {
    ignoreBuildErrors: true,
  },

  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'imgnews.pstatic.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'ssl.pstatic.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'lh3.googleusercontent.com',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'k.kakaocdn.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'http',
        hostname: 'img1.kakaocdn.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'img1.kakaocdn.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'http',
        hostname: 't1.kakaocdn.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 't1.kakaocdn.net',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'placehold.co',
        port: '',
        pathname: '/**',
      },
    ],
  },
  async rewrites() {
    // 개발환경에서는 localhost, 배포환경에서는 fly.dev 사용
    const isVercel = process.env.VERCEL === '1';

    const oauthCallback = isVercel
      ? 'https://news-ox.fly.dev/login/oauth2/code/:provider'
      : 'http://localhost:8080/login/oauth2/code/:provider';

    const oauthAuthorize = isVercel
      ? 'https://news-ox.fly.dev/oauth2/authorization/:provider'
      : 'http://localhost:8080/oauth2/authorization/:provider';

    const apiUrl = isVercel
      ? 'https://news-ox.fly.dev/api/:path*'
      : 'http://localhost:8080/api/:path*';

    return [
      // OAuth 콜백을 프론트 도메인에서 수신 → 백엔드로 프록시
      {
        source: '/api/login/oauth2/code/:provider',
        destination: oauthCallback,
      },
      // OAuth 시작 URL을 프론트 경유로 통일
      {
        source: '/api/oauth2/authorization/:provider',
        destination: oauthAuthorize,
      },
      // 일반 API 프록시
      {
        source: '/api/:path*',
        destination: apiUrl,
      },
    ];
  },
};

export default nextConfig;
