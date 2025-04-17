/** @type {import('next').NextConfig} */
const nextConfig = {
  /* config options here */
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://localhost:8080/api/:path*',
      },
      {
        source: '/ai/:path*',
        destination: 'http://localhost:8080/ai/:path*',
      },
    ];
  },
};

module.exports = nextConfig;
