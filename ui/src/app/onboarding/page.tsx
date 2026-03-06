"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  getOnboardingStats,
  getConnectors,
  getOnboardingUsers,
  type OnboardingStats,
  type ConnectorConfig,
  type OnboardingUser,
} from "@/lib/onboarding-api";

export default function OnboardingDashboard() {
  const [stats, setStats] = useState<OnboardingStats | null>(null);
  const [connectors, setConnectors] = useState<ConnectorConfig[]>([]);
  const [recentUsers, setRecentUsers] = useState<OnboardingUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [statsData, connectorsData, usersData] = await Promise.all([
        getOnboardingStats(),
        getConnectors(),
        getOnboardingUsers(),
      ]);
      setStats(statsData);
      setConnectors(connectorsData);
      setRecentUsers(usersData.slice(0, 5));
    } catch (err) {
      console.error("Failed to load dashboard data:", err);
      setError("Failed to load dashboard data. Make sure the backend is running.");
    } finally {
      setLoading(false);
    }
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
        <div className="animate-pulse text-slate-500">Loading dashboard...</div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Dashboard</h1>
          <p className="text-slate-500 mt-1">
            Overview of onboarding activity across all connectors
          </p>
        </div>
        <div className="flex space-x-3">
          <Link href="/onboarding/connectors">
            <Button variant="outline">Manage Connectors</Button>
          </Link>
          <Link href="/onboarding/users">
            <Button>Onboard New User</Button>
          </Link>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-slate-500">Total Users</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-slate-900">{stats?.totalUsers ?? 0}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-slate-500">Pending</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">{stats?.pending ?? 0}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-slate-500">In Progress</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">{stats?.inProgress ?? 0}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-slate-500">Completed</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{stats?.completed ?? 0}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-slate-500">Failed</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">{stats?.failed ?? 0}</div>
          </CardContent>
        </Card>
      </div>

      {/* Connectors Overview & Recent Users */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Active Connectors */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-lg">Active Connectors</CardTitle>
              <Link href="/onboarding/connectors">
                <Button variant="ghost" size="sm">View All</Button>
              </Link>
            </div>
          </CardHeader>
          <CardContent>
            {connectors.length === 0 ? (
              <div className="text-center py-8 text-slate-500">
                <p className="mb-2">No connectors configured yet</p>
                <Link href="/onboarding/connectors">
                  <Button variant="outline" size="sm">Add Connector</Button>
                </Link>
              </div>
            ) : (
              <div className="space-y-3">
                {connectors.map((connector) => (
                  <div
                    key={connector.id}
                    className="flex items-center justify-between p-3 bg-slate-50 rounded-lg"
                  >
                    <div className="flex items-center space-x-3">
                      <div
                        className={`w-2 h-2 rounded-full ${
                          connector.enabled ? "bg-green-500" : "bg-slate-300"
                        }`}
                      />
                      <div>
                        <p className="font-medium text-slate-900 text-sm">{connector.name}</p>
                        <p className="text-xs text-slate-500">{connector.connectorType}</p>
                      </div>
                    </div>
                    <Badge variant={connector.enabled ? "success" : "secondary"}>
                      {connector.enabled ? "Active" : "Disabled"}
                    </Badge>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Recent Users */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-lg">Recent Users</CardTitle>
              <Link href="/onboarding/users">
                <Button variant="ghost" size="sm">View All</Button>
              </Link>
            </div>
          </CardHeader>
          <CardContent>
            {recentUsers.length === 0 ? (
              <div className="text-center py-8 text-slate-500">
                <p className="mb-2">No users onboarded yet</p>
                <Link href="/onboarding/users">
                  <Button variant="outline" size="sm">Add User</Button>
                </Link>
              </div>
            ) : (
              <div className="space-y-3">
                {recentUsers.map((user) => (
                  <div
                    key={user.id}
                    className="flex items-center justify-between p-3 bg-slate-50 rounded-lg"
                  >
                    <div>
                      <p className="font-medium text-slate-900 text-sm">
                        {user.firstName} {user.lastName}
                      </p>
                      <p className="text-xs text-slate-500">{user.email}</p>
                    </div>
                    {getStatusBadge(user.status)}
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
