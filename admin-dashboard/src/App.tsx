import { useEffect, useState } from 'react';
import './App.css';

interface UsageLog {
  id: number;
  clientId: number;
  apiKeyId: number;
  path: string;
  method: string;
  status: number;
  timestamp: string;
  latencyMs: number;
}

function App() {
  const [logs, setLogs] = useState<UsageLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const apiKey = import.meta.env.VITE_API_KEY || '';

  useEffect(() => {
    fetch('/gateway/usage-logs', {
        headers: {
          'X-API-Key': apiKey
        }
      })
      .then((res) => {
        if (!res.ok) throw new Error('Network response was not ok');
        return res.json();
      })
      .then((data: UsageLog[]) => {
        setLogs(data);
        setLoading(false);
      })
      .catch((err: Error) => {
        setError(err.message);
        setLoading(false);
      });
  }, [apiKey]);

  if (loading) return <div className="loader">Loading usage logs…</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="container">
      <h1>🛡️ API Rate Limiter – Admin Dashboard</h1>
      <section>
        <h2>Usage Logs</h2>
        <table className="logs-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Client ID</th>
              <th>API‑Key ID</th>
              <th>Path</th>
              <th>Method</th>
              <th>Status</th>
              <th>Timestamp</th>
              <th>Latency (ms)</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td>{log.id}</td>
                <td>{log.clientId}</td>
                <td>{log.apiKeyId}</td>
                <td>{log.path}</td>
                <td>{log.method}</td>
                <td>{log.status}</td>
                <td>{new Date(log.timestamp).toLocaleString()}</td>
                <td>{log.latencyMs}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}

export default App;