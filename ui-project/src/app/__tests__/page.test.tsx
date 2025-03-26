import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import HomePage from '../page';

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('HomePage', () => {
  it('renders correctly', () => {
    render(<HomePage />);
    expect(screen.getByText(/Spring AI Chat/i)).toBeInTheDocument();
  });

  it('displays navigation links', () => {
    render(<HomePage />);
    expect(screen.getByText(/Go to Chat Interface/i)).toBeInTheDocument();
    expect(screen.getByText(/Project Manager/i)).toBeInTheDocument();
  });

  it('has proper layout with main section', () => {
    render(<HomePage />);
    expect(screen.getByRole('main')).toBeInTheDocument();
  });
});
