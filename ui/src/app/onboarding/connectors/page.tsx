"use client";

import React, { useState, useEffect, useCallback } from "react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";
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
  getConnectors,
  getConnectorTypes,
  createConnector,
  deleteConnector,
  toggleConnector,
  testConnector,
  type ConnectorConfig,
  type ConnectorType,
  type ExecutionResult,
} from "@/lib/onboarding-api";

// Connector config field definitions per type
const CONNECTOR_FIELDS: Record<string, { key: string; label: string; type: string; required: boolean }[]> = {
  AWS_DIRECTORY_SERVICE: [
    { key: "directoryId", label: "Directory ID", type: "text", required: true },
    { key: "region", label: "AWS Region", type: "text", required: true },
    { key: "accessKeyId", label: "Access Key ID", type: "password", required: true },
    { key: "secretAccessKey", label: "Secret Access Key", type: "password", required: true },
  ],
  AWS_WORKMAIL: [
    { key: "organizationId", label: "Organization ID", type: "text", required: true },
    { key: "region", label: "AWS Region", type: "text", required: true },
    { key: "domain", label: "Email Domain", type: "text", required: true },
    { key: "accessKeyId", label: "Access Key ID", type: "password", required: true },
    { key: "secretAccessKey", label: "Secret Access Key", type: "password", required: true },
  ],
  CLAUDE_AI: [
    { key: "apiKey", label: "API Key", type: "password", required: true },
    { key: "organizationId", label: "Organization ID", type: "text", required: true },
  ],
  JIRA: [
    { key: "baseUrl", label: "Jira Base URL", type: "text", required: true },
    { key: "apiToken", label: "API Token", type: "password", required: true },
    { key: "adminEmail", label: "Admin Email", type: "email", required: true },
    { key: "defaultProject", label: "Default Project Key", type: "text", required: false },
  ],
  ARMIS: [
    { key: "tenantUrl", label: "Tenant URL", type: "text", required: true },
    { key: "apiSecret", label: "API Secret", type: "password", required: true },
    { key: "defaultRole", label: "Default Role", type: "text", required: false },
  ],
  GITLAB: [
    { key: "baseUrl", label: "GitLab Base URL", type: "text", required: true },
    { key: "privateToken", label: "Private Token", type: "password", required: true },
    { key: "defaultGroup", label: "Default Group", type: "text", required: false },
    { key: "accessLevel", label: "Access Level", type: "text", required: false },
  ],
  CROWDSTRIKE: [
    { key: "clientId", label: "Client ID", type: "text", required: true },
    { key: "clientSecret", label: "Client Secret", type: "password", required: true },
    { key: "baseUrl", label: "Base URL", type: "text", required: true },
    { key: "defaultRole", label: "Default Role", type: "text", required: false },
  ],
};

const CONNECTOR_ICONS: Record<string, string> = {
  AWS_DIRECTORY_SERVICE: "DS",
  AWS_WORKMAIL: "WM",
  CLAUDE_AI: "CA",
  JIRA: "JI",
  ARMIS: "AR",
  GITLAB: "GL",
  CROWDSTRIKE: "CS",
};

const CONNECTOR_COLORS: Record<string, string> = {
  AWS_DIRECTORY_SERVICE: "bg-orange-500",
  AWS_WORKMAIL: "bg-orange-600",
  CLAUDE_AI: "bg-purple-600",
  JIRA: "bg-blue-600",
  ARMIS: "bg-teal-600",
  GITLAB: "bg-red-600",
  CROWDSTRIKE: "bg-red-500",
};

export default function ConnectorsPage() {
  const [connectors, setConnectors] = useState<ConnectorConfig[]>([]);
  const [connectorTypes, setConnectorTypes] = useState<ConnectorType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showTestResult, setShowTestResult] = useState(false);
  const [testResult, setTestResult] = useState<ExecutionResult | null>(null);
  const [testingId, setTestingId] = useState<number | null>(null);

  // Form state
  const [selectedType, setSelectedType] = useState<string>("");
  const [connectorName, setConnectorName] = useState("");
  const [configValues, setConfigValues] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [connectorsData, typesData] = await Promise.all([
        getConnectors(),
        getConnectorTypes(),
      ]);
      setConnectors(connectorsData);
      setConnectorTypes(typesData);
    } catch (err) {
      console.error("Failed to load connectors:", err);
      setError("Failed to load connectors. Ensure the backend is running.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleToggle = async (id: number) => {
    try {
      const updated = await toggleConnector(id);
      setConnectors((prev) =>
        prev.map((c) => (c.id === id ? updated : c))
      );
    } catch (err) {
      console.error("Failed to toggle connector:", err);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this connector?")) return;
    try {
      await deleteConnector(id);
      setConnectors((prev) => prev.filter((c) => c.id !== id));
    } catch (err) {
      console.error("Failed to delete connector:", err);
    }
  };

  const handleTest = async (id: number) => {
    setTestingId(id);
    try {
      const result = await testConnector(id);
      setTestResult(result);
      setShowTestResult(true);
    } catch (err) {
      setTestResult({ success: false, message: "Connection test failed: " + (err instanceof Error ? err.message : "Unknown error") });
      setShowTestResult(true);
    } finally {
      setTestingId(null);
    }
  };

  const handleAddConnector = async () => {
    if (!selectedType || !connectorName) return;
    setSaving(true);
    setError(null);
    try {
      const config = {
        name: connectorName,
        connectorType: selectedType,
        enabled: true,
        configJson: JSON.stringify(configValues),
      };
      const created = await createConnector(config);
      setConnectors((prev) => [...prev, created]);
      setShowAddDialog(false);
      resetForm();
    } catch (err) {
      setError("Failed to create connector: " + (err instanceof Error ? err.message : "Unknown error"));
    } finally {
      setSaving(false);
    }
  };

  const resetForm = () => {
    setSelectedType("");
    setConnectorName("");
    setConfigValues({});
  };

  const getTypeInfo = (type: string) => {
    return connectorTypes.find((t) => t.type === type);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-pulse text-slate-500">Loading connectors...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Connectors</h1>
          <p className="text-slate-500 mt-1">
            Configure and manage service connectors for automated onboarding
          </p>
        </div>
        <Button onClick={() => setShowAddDialog(true)}>Add Connector</Button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Connector Cards */}
      {connectors.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16">
            <p className="text-slate-500 mb-4 text-lg">No connectors configured yet</p>
            <p className="text-slate-400 mb-6 text-sm max-w-md text-center">
              Add connectors to automate user onboarding across services like AWS, Jira, GitLab, and more.
            </p>
            <Button onClick={() => setShowAddDialog(true)}>Add Your First Connector</Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {connectors.map((connector) => {
            const typeInfo = getTypeInfo(connector.connectorType);
            return (
              <Card key={connector.id} className="relative">
                <CardHeader className="pb-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                      <div
                        className={`w-10 h-10 rounded-lg flex items-center justify-center text-white font-bold text-xs ${
                          CONNECTOR_COLORS[connector.connectorType] || "bg-slate-500"
                        }`}
                      >
                        {CONNECTOR_ICONS[connector.connectorType] || "??"}
                      </div>
                      <div>
                        <CardTitle className="text-base">{connector.name}</CardTitle>
                        <CardDescription className="text-xs">
                          {typeInfo?.displayName || connector.connectorType}
                        </CardDescription>
                      </div>
                    </div>
                    <Switch
                      checked={connector.enabled}
                      onCheckedChange={() => handleToggle(connector.id)}
                    />
                  </div>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-slate-500 mb-4">
                    {typeInfo?.description || "Service connector"}
                  </p>
                  <div className="flex items-center justify-between">
                    <Badge variant={connector.enabled ? "success" : "secondary"}>
                      {connector.enabled ? "Active" : "Disabled"}
                    </Badge>
                    <div className="flex space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleTest(connector.id)}
                        disabled={testingId === connector.id}
                      >
                        {testingId === connector.id ? "Testing..." : "Test"}
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(connector.id)}
                      >
                        Delete
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>
      )}

      {/* Available Connector Types */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Available Connector Types</CardTitle>
          <CardDescription>
            These are the connector types supported by GSIC-TRACK
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
            {connectorTypes.map((type) => (
              <div
                key={type.type}
                className="flex items-center space-x-3 p-3 bg-slate-50 rounded-lg border border-slate-100"
              >
                <div
                  className={`w-8 h-8 rounded flex items-center justify-center text-white font-bold text-xs ${
                    CONNECTOR_COLORS[type.type] || "bg-slate-500"
                  }`}
                >
                  {CONNECTOR_ICONS[type.type] || "??"}
                </div>
                <div>
                  <p className="font-medium text-slate-900 text-sm">{type.displayName}</p>
                  <p className="text-xs text-slate-500">{type.description}</p>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Add Connector Dialog */}
      <Dialog open={showAddDialog} onOpenChange={setShowAddDialog}>
        <DialogContent className="max-w-lg max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Add Connector</DialogTitle>
            <DialogDescription>
              Configure a new service connector for automated onboarding
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">
            <div>
              <Label htmlFor="connector-type">Connector Type</Label>
              <select
                id="connector-type"
                className="flex h-10 w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-sm mt-1"
                value={selectedType}
                onChange={(e) => {
                  setSelectedType(e.target.value);
                  setConfigValues({});
                }}
              >
                <option value="">Select a connector type...</option>
                {connectorTypes.map((type) => (
                  <option key={type.type} value={type.type}>
                    {type.displayName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <Label htmlFor="connector-name">Connector Name</Label>
              <Input
                id="connector-name"
                placeholder="e.g., Production AWS Directory"
                value={connectorName}
                onChange={(e) => setConnectorName(e.target.value)}
                className="mt-1"
              />
            </div>

            {selectedType && CONNECTOR_FIELDS[selectedType] && (
              <div className="space-y-3 pt-2 border-t">
                <p className="text-sm font-medium text-slate-700">Configuration</p>
                {CONNECTOR_FIELDS[selectedType].map((field) => (
                  <div key={field.key}>
                    <Label htmlFor={field.key}>
                      {field.label}
                      {field.required && <span className="text-red-500 ml-1">*</span>}
                    </Label>
                    <Input
                      id={field.key}
                      type={field.type}
                      placeholder={field.label}
                      value={configValues[field.key] || ""}
                      onChange={(e) =>
                        setConfigValues((prev) => ({
                          ...prev,
                          [field.key]: e.target.value,
                        }))
                      }
                      className="mt-1"
                    />
                  </div>
                ))}
              </div>
            )}
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => { setShowAddDialog(false); resetForm(); }}>
              Cancel
            </Button>
            <Button
              onClick={handleAddConnector}
              disabled={!selectedType || !connectorName || saving}
            >
              {saving ? "Saving..." : "Add Connector"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Test Result Dialog */}
      <Dialog open={showTestResult} onOpenChange={setShowTestResult}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Connection Test Result</DialogTitle>
          </DialogHeader>
          {testResult && (
            <div
              className={`p-4 rounded-lg ${
                testResult.success
                  ? "bg-green-50 border border-green-200"
                  : "bg-red-50 border border-red-200"
              }`}
            >
              <div className="flex items-center space-x-2 mb-2">
                <Badge variant={testResult.success ? "success" : "destructive"}>
                  {testResult.success ? "Success" : "Failed"}
                </Badge>
              </div>
              <p className={`text-sm ${testResult.success ? "text-green-700" : "text-red-700"}`}>
                {testResult.message}
              </p>
            </div>
          )}
          <DialogFooter>
            <Button onClick={() => setShowTestResult(false)}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
