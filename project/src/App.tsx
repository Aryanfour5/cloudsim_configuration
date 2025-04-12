import React, { useState } from 'react';
import axios from 'axios';
import { Cloud, Server, Users, MemoryStick as Memory, Timer, PlayCircle, DownloadCloud } from 'lucide-react';
import { saveAs } from 'file-saver'; // For downloading CSV
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import html2canvas from 'html2canvas';

// Register chart components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface SimulationResult {
  strategy: string;
  makespan: number;
  energyConsumption: number;
  cost: number;
  slaViolations: number;
  startTime: string;
  endTime: string;
}

interface SimulationConfig {
  numberOfUsers: number;
  numberOfVms: number;
  numberOfCloudlets: number;
  vmRam: number;
  slaThreshold: number;
  strategies: string[];
}

interface InputFieldProps {
  label: string;
  icon: React.ComponentType;
  type: string;
  value: number;
  onChange: (value: number) => void;
  min?: number;
  step?: number;
}

const InputField: React.FC<InputFieldProps> = ({
  label,
  icon: Icon,
  type,
  value,
  onChange,
  min,
  step,
}) => (
  <div className="flex flex-col">
    <label className="text-sm font-medium text-gray-700">{label}</label>
    <div className="flex items-center border border-gray-300 rounded-md mt-1">
      <Icon className="text-gray-400 ml-3" size={20} />
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(Number(e.target.value))}
        className="w-full py-2 px-3 text-sm text-gray-800 border-none rounded-md outline-none"
        min={min}
        step={step}
      />
    </div>
  </div>
);

function App() {
  const [config, setConfig] = useState<SimulationConfig>({
    numberOfUsers: 1,
    numberOfVms: 1,
    numberOfCloudlets: 1,
    vmRam: 1024,
    slaThreshold: 0.95,
    strategies: [],
  });

  const [results, setResults] = useState<SimulationResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const strategies = ['BestFit', 'RoundRobin', 'TimeShared', 'SpaceShared', 'PriorityBased'];

  const handleStrategyChange = (strategy: string) => {
    setConfig(prev => ({
      ...prev,
      strategies: prev.strategies.includes(strategy)
        ? prev.strategies.filter(s => s !== strategy)
        : [...prev.strategies, strategy],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const startTime = new Date().toISOString();
      const response = await axios.post('http://localhost:8081/api/simulate', config);
      const endTime = new Date().toISOString();

      const mappedResults = response.data.map((result: any) => ({
        strategy: result.strategy,
        makespan: result.executionTime,
        energyConsumption: result.energyConsumption,
        cost: result.cost,
        slaViolations: result.slaViolation,
        startTime,
        endTime,
      }));

      setResults(mappedResults);
    } catch (err) {
      setError('Failed to run simulation. Please try again.');
      console.error("Simulation error:", err);
    } finally {
      setLoading(false);
    }
  };

  const generateCSV = () => {
    const csvHeader = [
      'Strategy', 'Makespan', 'Energy Consumption', 'Cost', 'SLA Violations', 'Start Time', 'End Time',
    ];
    const csvRows = results.map(result => [
      result.strategy,
      result.makespan,
      result.energyConsumption,
      result.cost,
      result.slaViolations,
      result.startTime,
      result.endTime,
    ]);

    const csvContent = [csvHeader, ...csvRows].map(row => row.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
    saveAs(blob, 'simulation_results.csv');
  };

  const graphData = {
    makespanData: {
      labels: results.map(r => r.strategy),
      datasets: [
        {
          label: 'Makespan',
          data: results.map(r => r.makespan),
          backgroundColor: 'rgba(75,192,192,0.5)',
          borderColor: 'rgba(75,192,192,1)',
          borderWidth: 1,
        },
      ],
    },
    energyData: {
      labels: results.map(r => r.strategy),
      datasets: [
        {
          label: 'Energy Consumption',
          data: results.map(r => r.energyConsumption),
          backgroundColor: 'rgba(153,102,255,0.5)',
          borderColor: 'rgba(153,102,255,1)',
          borderWidth: 1,
        },
      ],
    },
    costData: {
      labels: results.map(r => r.strategy),
      datasets: [
        {
          label: 'Cost',
          data: results.map(r => r.cost),
          backgroundColor: 'rgba(255,159,64,0.5)',
          borderColor: 'rgba(255,159,64,1)',
          borderWidth: 1,
        },
      ],
    },
  };

  // New function to download all charts
  const downloadAllCharts = async () => {
    try {
      // Ensure charts are fully rendered
      const chartElements = document.querySelectorAll('.chart-container');

      for (let index = 0; index < chartElements.length; index++) {
        const chartElement = chartElements[index];
        const canvas = await html2canvas(chartElement);
        
        // Create an image from the rendered canvas
        const image = canvas.toDataURL('image/png');

        // Create a link element for downloading the image
        const link = document.createElement('a');
        link.href = image;
        link.download = `chart_${index + 1}.png`;

        // Trigger the download
        link.click();
      }
    } catch (error) {
      console.error('Error downloading charts:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600 mb-3">
            CloudSim Configuration
          </h1>
          <p className="text-gray-600 text-lg">Configure and run your cloud simulations with ease</p>
        </div>

        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl p-8 mb-8 border border-gray-100">
          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <InputField label="Number of Users" icon={Users} type="number" value={config.numberOfUsers} onChange={value => setConfig(prev => ({ ...prev, numberOfUsers: value }))} min={1} />
              <InputField label="Number of VMs" icon={Server} type="number" value={config.numberOfVms} onChange={value => setConfig(prev => ({ ...prev, numberOfVms: value }))} min={1} />
              <InputField label="Number of Cloudlets" icon={Cloud} type="number" value={config.numberOfCloudlets} onChange={value => setConfig(prev => ({ ...prev, numberOfCloudlets: value }))} min={1} />
              <InputField label="VM RAM (MB)" icon={Memory} type="number" value={config.vmRam} onChange={value => setConfig(prev => ({ ...prev, vmRam: value }))} min={512} step={512} />
              <InputField label="SLA Threshold" icon={Timer} type="number" value={config.slaThreshold} onChange={value => setConfig(prev => ({ ...prev, slaThreshold: value }))} min={0} step={0.01} />
            </div>

            <div className="mt-8 bg-gray-50 p-6 rounded-xl border border-gray-100">
              <label className="block text-sm font-medium text-gray-700 mb-3">Simulation Strategies</label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                {strategies.map(strategy => (
                  <label key={strategy} className="flex items-center space-x-2">
                    <input
                      type="checkbox"
                      checked={config.strategies.includes(strategy)}
                      onChange={() => handleStrategyChange(strategy)}
                      className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                    />
                    <span className="text-sm text-gray-700">{strategy}</span>
                  </label>
                ))}
              </div>
            </div>

            <div className="mt-8">
              <button
                type="submit"
                disabled={loading || config.strategies.length === 0}
                className="w-full flex justify-center items-center gap-2 py-3 px-4 rounded-xl text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 focus:outline-none disabled:bg-gray-400 transition-all shadow-md"
              >
                <PlayCircle size={20} />
                {loading ? 'Running Simulation...' : 'Run Simulation'}
              </button>
            </div>
          </form>
        </div>

        {error && (
          <div className="bg-red-100 text-red-700 border border-red-300 p-4 rounded mb-8">
            {error}
          </div>
        )}

        {results.length > 0 && (
          <div className="space-y-10">
            <div className="bg-white/80 rounded-2xl p-6 shadow-xl border border-gray-100 overflow-x-auto">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Simulation Results</h2>
              <table className="min-w-full table-auto text-sm text-left text-gray-600">
                <thead className="bg-gray-100 text-xs uppercase font-semibold">
                  <tr>
                    <th className="px-4 py-2">Strategy</th>
                    <th className="px-4 py-2">Makespan</th>
                    <th className="px-4 py-2">Energy Consumption</th>
                    <th className="px-4 py-2">Cost</th>
                    <th className="px-4 py-2">SLA Violations</th>
                    <th className="px-4 py-2">Start Time</th>
                    <th className="px-4 py-2">End Time</th>
                  </tr>
                </thead>
                <tbody>
                  {results.map((result, idx) => (
                    <tr key={idx} className="hover:bg-gray-50">
                      <td className="px-4 py-2">{result.strategy}</td>
                      <td className="px-4 py-2">{result.makespan}</td>
                      <td className="px-4 py-2">{result.energyConsumption}</td>
                      <td className="px-4 py-2">{result.cost}</td>
                      <td className="px-4 py-2">{result.slaViolations}</td>
                      <td className="px-4 py-2">{result.startTime}</td>
                      <td className="px-4 py-2">{result.endTime}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Makespan Chart */}
            <div className="chart-container bg-white/80 rounded-2xl p-6 shadow-xl border border-gray-100">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Makespan Comparison</h2>
              <Bar data={graphData.makespanData} options={{ responsive: true, plugins: { legend: { position: 'top' } } }} />
            </div>

            {/* Energy Consumption Chart */}
            <div className="chart-container bg-white/80 rounded-2xl p-6 shadow-xl border border-gray-100">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Energy Consumption Comparison</h2>
              <Bar data={graphData.energyData} options={{ responsive: true, plugins: { legend: { position: 'top' } } }} />
            </div>

            {/* Cost Chart */}
            <div className="chart-container bg-white/80 rounded-2xl p-6 shadow-xl border border-gray-100">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Cost Comparison</h2>
              <Bar data={graphData.costData} options={{ responsive: true, plugins: { legend: { position: 'top' } } }} />
            </div>

            <div className="text-center">
              <button
                onClick={generateCSV}
                className="inline-flex items-center px-6 py-3 rounded-xl bg-green-600 hover:bg-green-700 text-white text-sm font-semibold shadow-lg transition-all"
              >
                <DownloadCloud className="mr-2" size={18} />
                Download CSV
              </button>
            </div>

            <div className="text-center mt-8">
              <button
                onClick={downloadAllCharts}
                className="inline-flex items-center px-6 py-3 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold shadow-lg transition-all"
              >
                <DownloadCloud className="mr-2" size={18} />
                Download All Charts
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
