/** @type {import('next').NextConfig} */
const nextConfig = {
  webpack: (config) => {
    config.resolve.extensions = [...config.resolve.extensions, '.ts', '.tsx'];
    return config;
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://localhost:8090/api/:path*'
      },
    ];
  },
  serverRuntimeConfig: {
    port: process.env.PORT || 3000,
  },
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8090/api'
  }
};

module.exports = nextConfig; 