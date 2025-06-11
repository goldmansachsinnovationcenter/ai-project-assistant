'use client';

import React, { useState } from 'react';
import { Project } from '../lib/api';
import ConfirmDialog from './ConfirmDialog';

interface ProjectActionsProps {
  project: Project;
  onEdit?: (project: Project) => void;
  onDelete?: (project: Project) => void;
}

export default function ProjectActions({ project, onEdit, onDelete }: ProjectActionsProps) {
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const handleEdit = () => {
    if (onEdit) {
      onEdit(project);
    }
  };

  const handleDelete = () => {
    setShowDeleteDialog(true);
  };

  const confirmDelete = () => {
    if (onDelete) {
      onDelete(project);
    }
    setShowDeleteDialog(false);
  };

  return (
    <>
      <div className="flex space-x-2">
        <button
          onClick={handleEdit}
          className="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded hover:bg-blue-200 transition-colors"
          title="Edit Project"
        >
          Edit
        </button>
        <button
          onClick={handleDelete}
          className="px-3 py-1 text-sm bg-red-100 text-red-700 rounded hover:bg-red-200 transition-colors"
          title="Delete Project"
        >
          Delete
        </button>
      </div>

      <ConfirmDialog
        isOpen={showDeleteDialog}
        title="Delete Project"
        message={`Are you sure you want to delete "${project.name}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        isDestructive={true}
        onConfirm={confirmDelete}
        onCancel={() => setShowDeleteDialog(false)}
      />
    </>
  );
}
