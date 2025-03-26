import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import ChatPage from '../page';

// Mock ChatInterface component directly without using path alias
jest.mock('../../../components/ChatInterface', () => {
  return function MockChatInterface() {
    return <div data-testid="chat-interface">Chat Interface Mock</div>;
  };
});

describe('ChatPage', () => {
  it('renders the chat page with ChatInterface component', () => {
    render(<ChatPage />);
    expect(screen.getByTestId('chat-interface')).toBeInTheDocument();
  });

  it('has proper layout with heading', () => {
    render(<ChatPage />);
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toBeInTheDocument();
  });
  
  it('renders the page container with proper styling', () => {
    render(<ChatPage />);
    const mainElement = screen.getByRole('main');
    expect(mainElement).toBeInTheDocument();
    expect(mainElement).toHaveClass('min-h-screen');
  });
});
