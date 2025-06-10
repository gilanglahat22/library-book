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
        destination: 'http://localhost:8080/api/:path*',
      },
    ];
  },
  serverRuntimeConfig: {
    port: process.env.PORT || 3002,
  },
};

module.exports = nextConfig; 