import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import ChatInterface from '../ChatInterface';
import * as api from '../../lib/api';

// Mock the API module
jest.mock('../../lib/api', () => ({
  sendMessage: jest.fn(),
  getChatHistory: jest.fn()
}));

// Mock scrollIntoView
window.HTMLElement.prototype.scrollIntoView = jest.fn();

describe('ChatInterface', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock the API responses
    (api.sendMessage as jest.Mock).mockResolvedValue('This is a test response');
    (api.getChatHistory as jest.Mock).mockResolvedValue([
      { prompt: 'Previous message', response: 'Previous response' }
    ]);
  });

  test('renders chat interface', async () => {
    await act(async () => {
      render(<ChatInterface />);
    });
    
    // Check for basic elements
    expect(screen.getByText('Chat with AI')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Type your message here...')).toBeInTheDocument();
    
    // Wait for chat history to load
    await waitFor(() => {
      expect(screen.queryByText('Loading chat history...')).not.toBeInTheDocument();
    });
  });

  test('handles empty input', async () => {
    await act(async () => {
      render(<ChatInterface />);
    });
    
    // Wait for chat history to load
    await waitFor(() => {
      expect(screen.queryByText('Loading chat history...')).not.toBeInTheDocument();
    });
    
    // Try to submit with empty input
    const submitButton = screen.getByRole('button', { name: 'Send' });
    expect(submitButton).toBeDisabled();
  });

  test('handles error state', async () => {
    // Mock API error
    (api.getChatHistory as jest.Mock).mockRejectedValue(new Error('API error'));
    
    await act(async () => {
      render(<ChatInterface />);
    });
    
    // Wait for error message
    await waitFor(() => {
      expect(screen.getByText(/Failed to load chat history/)).toBeInTheDocument();
    });
  });

  test('sends message successfully', async () => {
    await act(async () => {
      render(<ChatInterface />);
    });
    
    // Wait for chat history to load
    await waitFor(() => {
      expect(screen.queryByText('Loading chat history...')).not.toBeInTheDocument();
    });
    
    // Type a message
    const input = screen.getByPlaceholderText('Type your message here...');
    await act(async () => {
      fireEvent.change(input, { target: { value: 'Test message' } });
    });
    
    // Submit the form
    const form = screen.getByTestId('chat-form');
    await act(async () => {
      fireEvent.submit(form);
    });
    
    // Check API was called
    expect(api.sendMessage).toHaveBeenCalledWith('Test message');
    
    // Check input is cleared
    await waitFor(() => {
      expect(input).toHaveValue('');
    });
  });
});
