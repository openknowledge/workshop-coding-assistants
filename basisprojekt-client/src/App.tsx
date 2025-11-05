import { useCallback, useEffect, useState } from 'react';
import reactLogo from './assets/react.svg';
import viteLogo from '/vite.svg';
import './App.css';

function App() {
  const [count, setCount] = useState<number | undefined>();

  const fetchCount = async () => {
    try {
      const response = await fetch('api/counters/example');
      const value = await response.json();
      setCount(Number(value));
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchCount();
  }, []);

  const updateCount = useCallback(() => {
    setCount((count) => {
      const newCount = (count ?? 0) + 1;
      fetch('api/counters/example', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: String(newCount),
      }).catch(console.error);
      return newCount;
    });
  }, []);

  return (
    <>
      <div>
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React</h1>
      <div className="card">
        {count !== undefined ? (
          <button onClick={updateCount}>count is {count}</button>
        ) : (
          <p>Loading...</p>
        )}
      </div>
    </>
  );
}

export default App;
