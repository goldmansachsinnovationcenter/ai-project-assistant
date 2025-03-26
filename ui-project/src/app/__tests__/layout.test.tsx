import React from 'react';
import { render, screen } from '@testing-library/react';
import RootLayout from '../layout';

describe('RootLayout', () => {
  test('renders layout with children', () => {
    render(
      <RootLayout>
        <div>Test Content</div>
      </RootLayout>
    );
    
    // Check that the children are rendered
    expect(screen.getByText('Test Content')).toBeInTheDocument();
  });
});
