import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import {ApplicationProvider} from "./contexts/ApplicationProvider.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
  // <React.StrictMode>
    <ApplicationProvider>
        <App />
    </ApplicationProvider>
  // </React.StrictMode>,
)
