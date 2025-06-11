'use client';

import React from 'react';

interface FloatingActionButtonProps {
  onClick: () => void;
  icon?: React.ReactNode;
  label?: string;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  color?: 'blue' | 'green' | 'red' | 'purple' | 'yellow';
}

export default function FloatingActionButton({
  onClick,
  icon,
  label,
  className = '',
  size = 'md',
  color = 'blue'
}: FloatingActionButtonProps) {
  const sizeClasses = {
    sm: 'w-10 h-10 text-sm',
    md: 'w-12 h-12 text-base',
    lg: 'w-14 h-14 text-lg'
  };

  const colorClasses = {
    blue: 'bg-blue-600 hover:bg-blue-700 focus:ring-blue-500',
    green: 'bg-green-600 hover:bg-green-700 focus:ring-green-500',
    red: 'bg-red-600 hover:bg-red-700 focus:ring-red-500',
    purple: 'bg-purple-600 hover:bg-purple-700 focus:ring-purple-500',
    yellow: 'bg-yellow-600 hover:bg-yellow-700 focus:ring-yellow-500'
  };

  return (
    <button
      onClick={onClick}
      className={`
        ${sizeClasses[size]}
        ${colorClasses[color]}
        rounded-full
        text-white
        shadow-lg
        hover:shadow-xl
        focus:outline-none
        focus:ring-4
        focus:ring-opacity-50
        transition-all
        duration-200
        flex
        items-center
        justify-center
        backdrop-blur
        transition-backdrop
        ${className}
      `}
      title={label}
    >
      {icon || '+'}
    </button>
  );
}
