import { Toaster } from 'react-hot-toast';
import './App.css';
import { AllRoutes } from "./routes/AllRoutes"


function App() {
  return (
    <div className="App">
      <AllRoutes />
      <Toaster/>
    </div>
  );
}

export default App;
