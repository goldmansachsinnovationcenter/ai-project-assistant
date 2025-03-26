import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SummaryPage from '../page';
import * as api from '../../../lib/api';

// Mock the API module
jest.mock('../../../lib/api', () => ({
  getTopicSummary: jest.fn()
}));

describe('SummaryPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders summary page with form', () => {
    render(<SummaryPage />);
    
    expect(screen.getByText('Topic Summary Tool')).toBeInTheDocument();
    expect(screen.getByLabelText('Topic')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter a topic for summary...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Get Summary' })).toBeInTheDocument();
  });

  test('button is disabled when topic is empty', () => {
    render(<SummaryPage />);
    
    const button = screen.getByRole('button', { name: 'Get Summary' });
    expect(button).toBeDisabled();
  });

  test('button is enabled when topic is entered', async () => {
    render(<SummaryPage />);
    
    const input = screen.getByLabelText('Topic');
    await userEvent.type(input, 'AI');
    
    const button = screen.getByRole('button', { name: 'Get Summary' });
    expect(button).not.toBeDisabled();
  });

  test('submits form and displays summary', async () => {
    (api.getTopicSummary as jest.Mock).mockResolvedValue('This is a summary about AI.');
    
    render(<SummaryPage />);
    
    const input = screen.getByLabelText('Topic');
    await userEvent.type(input, 'AI');
    
    const button = screen.getByRole('button', { name: 'Get Summary' });
    fireEvent.click(button);
    
    expect(screen.getByRole('button', { name: 'Generating...' })).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByText('Summary:')).toBeInTheDocument();
      expect(screen.getByText('This is a summary about AI.')).toBeInTheDocument();
    });
    
    expect(api.getTopicSummary).toHaveBeenCalledWith('AI');
  });

  test('handles API error', async () => {
    // Mock API error
    (api.getTopicSummary as jest.Mock).mockRejectedValue(new Error('API error'));
    
    render(<SummaryPage />);
    
    const input = screen.getByLabelText('Topic');
    await userEvent.type(input, 'AI');
    
    const button = screen.getByRole('button', { name: 'Get Summary' });
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.getByText('Failed to get summary from AI. Please try again.')).toBeInTheDocument();
    });
  });

  test('prevents form submission with empty topic', async () => {
    render(<SummaryPage />);
    
    const button = screen.getByRole('button', { name: 'Get Summary' });
    fireEvent.click(button);
    
    expect(api.getTopicSummary).not.toHaveBeenCalled();
  });

  test('clears error when submitting form again', async () => {
    (api.getTopicSummary as jest.Mock)
      .mockRejectedValueOnce(new Error('API error'))
      .mockResolvedValueOnce('This is a summary about AI.');
    
    render(<SummaryPage />);
    
    const input = screen.getByLabelText('Topic');
    await userEvent.type(input, 'AI');
    
    const button = screen.getByRole('button', { name: 'Get Summary' });
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.getByText('Failed to get summary from AI. Please try again.')).toBeInTheDocument();
    });
    
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.queryByText('Failed to get summary from AI. Please try again.')).not.toBeInTheDocument();
    });
  });
});
