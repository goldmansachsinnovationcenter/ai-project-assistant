'use client';

import React, { useEffect } from 'react';

interface KeyboardShortcutsProps {
  onCreateProject?: () => void;
  onSearch?: () => void;
  onHome?: () => void;
}

export default function KeyboardShortcuts({ onCreateProject, onSearch, onHome }: KeyboardShortcutsProps) {
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.ctrlKey || event.metaKey) {
        switch (event.key) {
          case 'n':
            event.preventDefault();
            if (onCreateProject) onCreateProject();
            break;
          case 'k':
            event.preventDefault();
            if (onSearch) onSearch();
            break;
          case 'h':
            event.preventDefault();
            if (onHome) onHome();
            break;
        }
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [onCreateProject, onSearch, onHome]);

  return null;
}
