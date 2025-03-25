// Add fetch mock setup
import 'jest-fetch-mock';

// Add Jest DOM matchers
import '@testing-library/jest-dom';

// Mock next/navigation
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    prefetch: jest.fn()
  }),
  useParams: () => ({}),
  usePathname: () => ''
}));

// Configure Jest DOM
import { configure } from '@testing-library/react';
configure({ testIdAttribute: 'data-testid' });
