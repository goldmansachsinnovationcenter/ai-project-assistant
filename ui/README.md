# UI Project for Spring AI

This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app) that provides a user interface for the Spring AI project management system. It features a chat interface for AI interaction and project management capabilities.

## Features

- AI chat interface
- Project management dashboard
- User story visualization
- Requirements tracking
- Responsive design with Tailwind CSS

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

## Project Structure

- `/src/app` - Next.js pages
- `/src/components` - Reusable UI components
- `/src/lib` - Utility functions and API client

## Test Coverage

To run tests with coverage reporting:

```bash
npm run test:coverage
```

The coverage report will be generated at `coverage/lcov-report/index.html`.

Coverage requirements:
- Line coverage: 90%
- Branch coverage: 90%
- Function coverage: 90%

## Integrated Testing

A combined test script is available at the root of the project:

```bash
/home/ubuntu/run_coverage_tests.sh
```

This script runs both backend and frontend tests with coverage reporting.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.
