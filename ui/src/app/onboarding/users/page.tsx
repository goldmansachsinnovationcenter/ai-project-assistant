"use client";

import React, { useState, useEffect, useCallback } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  getOnboardingUsers,
  createOnboardingUser,
  deleteOnboardingUser,
  executeOnboarding,
  getTasksForUser,
  retryTask,
  type OnboardingUser,
  type OnboardingTask,
} from "@/lib/onboarding-api";

const CONNECTOR_LABELS: Record<string, string> = {
  AWS_DIRECTORY_SERVICE: "AWS Directory Service",
  AWS_WORKMAIL: "AWS WorkMail",
  CLAUDE_AI: "Claude AI",
  JIRA: "Jira",
  ARMIS: "Armis",
  GITLAB: "GitLab",
  CROWDSTRIKE: "CrowdStrike",
};

export default function UsersPage() {
  const [users, setUsers] = useState<OnboardingUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showTasksDialog, setShowTasksDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState<OnboardingUser | null>(null);
  const [userTasks, setUserTasks] = useState<OnboardingTask[]>([]);
  const [executingId, setExecutingId] = useState<number | null>(null);
  const [retryingTaskId, setRetryingTaskId] = useState<number | null>(null);

  // Form state
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [department, setDepartment] = useState("");
  const [jobTitle, setJobTitle] = useState("");
  const [saving, setSaving] = useState(false);

  const loadUsers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getOnboardingUsers();
      setUsers(data);
    } catch (err) {
      console.error("Failed to load users:", err);
      setError("Failed to load users. Ensure the backend is running.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const handleAddUser = async () => {
    if (!firstName || !lastName || !email) return;
    setSaving(true);
    setError(null);
    try {
      const user = await createOnboardingUser({
        firstName,
        lastName,
        email,
        department,
        jobTitle,
      });
      setUsers((prev) => [...prev, user]);
      setShowAddDialog(false);
      resetForm();
    } catch (err) {
      setError(
        "Failed to create user: " +
          (err instanceof Error ? err.message : "Unknown error")
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this user?")) return;
    try {
      await deleteOnboardingUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
    } catch (err) {
      console.error("Failed to delete user:", err);
    }
  };

  const handleExecute = async (id: number) => {
    setExecutingId(id);
    setError(null);
    try {
      const updatedUser = await executeOnboarding(id);
      setUsers((prev) => prev.map((u) => (u.id === id ? updatedUser : u)));
    } catch (err) {
      setError(
        "Onboarding failed: " +
          (err instanceof Error ? err.message : "Unknown error")
      );
    } finally {
      setExecutingId(null);
    }
  };

  const handleViewTasks = async (user: OnboardingUser) => {
    setSelectedUser(user);
    try {
      const tasks = await getTasksForUser(user.id);
      setUserTasks(tasks);
    } catch {
      setUserTasks(user.tasks || []);
    }
    setShowTasksDialog(true);
  };

  const handleRetryTask = async (taskId: number) => {
    setRetryingTaskId(taskId);
    try {
      const updatedTask = await retryTask(taskId);
      setUserTasks((prev) =>
        prev.map((t) => (t.id === taskId ? updatedTask : t))
      );
      // Refresh users list
      await loadUsers();
    } catch (err) {
      console.error("Failed to retry task:", err);
    } finally {
      setRetryingTaskId(null);
    }
  };

  const resetForm = () => {
    setFirstName("");
    setLastName("");
    setEmail("");
    setDepartment("");
    setJobTitle("");
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "COMPLETED":
        return <Badge variant="success">Completed</Badge>;
      case "FAILED":
        return <Badge variant="destructive">Failed</Badge>;
      case "IN_PROGRESS":
        return <Badge variant="default">In Progress</Badge>;
      case "PARTIALLY_COMPLETED":
        return <Badge variant="warning">Partial</Badge>;
      default:
        return <Badge variant="secondary">Pending</Badge>;
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-pulse text-slate-500">Loading users...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Users</h1>
          <p className="text-slate-500 mt-1">
            Manage user onboarding across all enabled connectors
          </p>
        </div>
        <Button onClick={() => setShowAddDialog(true)}>Add User</Button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Users Table */}
      {users.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16">
            <p className="text-slate-500 mb-4 text-lg">No users to onboard yet</p>
            <p className="text-slate-400 mb-6 text-sm max-w-md text-center">
              Add users and execute onboarding to automatically provision them across all enabled connectors.
            </p>
            <Button onClick={() => setShowAddDialog(true)}>Add Your First User</Button>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">
              Onboarding Users ({users.length})
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-200">
                    <th className="text-left py-3 px-4 font-medium text-slate-500">Name</th>
                    <th className="text-left py-3 px-4 font-medium text-slate-500">Email</th>
                    <th className="text-left py-3 px-4 font-medium text-slate-500">Department</th>
                    <th className="text-left py-3 px-4 font-medium text-slate-500">Status</th>
                    <th className="text-left py-3 px-4 font-medium text-slate-500">Created</th>
                    <th className="text-right py-3 px-4 font-medium text-slate-500">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id} className="border-b border-slate-100 hover:bg-slate-50">
                      <td className="py-3 px-4">
                        <div>
                          <p className="font-medium text-slate-900">
                            {user.firstName} {user.lastName}
                          </p>
                          {user.jobTitle && (
                            <p className="text-xs text-slate-500">{user.jobTitle}</p>
                          )}
                        </div>
                      </td>
                      <td className="py-3 px-4 text-slate-600">{user.email}</td>
                      <td className="py-3 px-4 text-slate-600">
                        {user.department || "-"}
                      </td>
                      <td className="py-3 px-4">{getStatusBadge(user.status)}</td>
                      <td className="py-3 px-4 text-slate-500 text-xs">
                        {user.createdAt
                          ? new Date(user.createdAt).toLocaleDateString()
                          : "-"}
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex items-center justify-end space-x-2">
                          {user.status === "PENDING" && (
                            <Button
                              size="sm"
                              onClick={() => handleExecute(user.id)}
                              disabled={executingId === user.id}
                            >
                              {executingId === user.id ? "Running..." : "Onboard"}
                            </Button>
                          )}
                          {(user.status === "COMPLETED" ||
                            user.status === "FAILED" ||
                            user.status === "PARTIALLY_COMPLETED") && (
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleViewTasks(user)}
                            >
                              View Tasks
                            </Button>
                          )}
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => handleDelete(user.id)}
                          >
                            Delete
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Add User Dialog */}
      <Dialog open={showAddDialog} onOpenChange={setShowAddDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add User for Onboarding</DialogTitle>
            <DialogDescription>
              Enter the user details to add them to the onboarding queue
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="first-name">
                  First Name <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="first-name"
                  placeholder="John"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  className="mt-1"
                />
              </div>
              <div>
                <Label htmlFor="last-name">
                  Last Name <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="last-name"
                  placeholder="Doe"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  className="mt-1"
                />
              </div>
            </div>

            <div>
              <Label htmlFor="email">
                Email <span className="text-red-500">*</span>
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="john.doe@company.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="mt-1"
              />
            </div>

            <div>
              <Label htmlFor="department">Department</Label>
              <Input
                id="department"
                placeholder="Engineering"
                value={department}
                onChange={(e) => setDepartment(e.target.value)}
                className="mt-1"
              />
            </div>

            <div>
              <Label htmlFor="job-title">Job Title</Label>
              <Input
                id="job-title"
                placeholder="Software Engineer"
                value={jobTitle}
                onChange={(e) => setJobTitle(e.target.value)}
                className="mt-1"
              />
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => {
                setShowAddDialog(false);
                resetForm();
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleAddUser}
              disabled={!firstName || !lastName || !email || saving}
            >
              {saving ? "Adding..." : "Add User"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Tasks Dialog */}
      <Dialog open={showTasksDialog} onOpenChange={setShowTasksDialog}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>
              Onboarding Tasks -{" "}
              {selectedUser
                ? `${selectedUser.firstName} ${selectedUser.lastName}`
                : ""}
            </DialogTitle>
            <DialogDescription>
              View the status of each connector task for this user
            </DialogDescription>
          </DialogHeader>

          {userTasks.length === 0 ? (
            <p className="text-slate-500 text-center py-4">No tasks found</p>
          ) : (
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {userTasks.map((task) => (
                <div
                  key={task.id}
                  className="p-4 bg-slate-50 rounded-lg border border-slate-100"
                >
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center space-x-2">
                      <span className="font-medium text-slate-900 text-sm">
                        {CONNECTOR_LABELS[task.connectorType] || task.connectorType}
                      </span>
                      {getStatusBadge(task.status)}
                    </div>
                    {task.status === "FAILED" && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleRetryTask(task.id)}
                        disabled={retryingTaskId === task.id}
                      >
                        {retryingTaskId === task.id ? "Retrying..." : "Retry"}
                      </Button>
                    )}
                  </div>
                  {task.resultMessage && (
                    <p className="text-sm text-green-700 bg-green-50 p-2 rounded mt-1">
                      {task.resultMessage}
                    </p>
                  )}
                  {task.errorMessage && (
                    <p className="text-sm text-red-700 bg-red-50 p-2 rounded mt-1">
                      {task.errorMessage}
                    </p>
                  )}
                  {task.externalId && (
                    <p className="text-xs text-slate-400 mt-1">
                      External ID: {task.externalId}
                    </p>
                  )}
                </div>
              ))}
            </div>
          )}

          <DialogFooter>
            <Button onClick={() => setShowTasksDialog(false)}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
