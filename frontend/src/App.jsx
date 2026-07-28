import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home.jsx'
import Admin from './pages/Admin.jsx'
import Navbar from './components/Navbar.jsx'
import Footer from './components/Footer.jsx'

export default function App() {
  return (
    <div className="app-shell">
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/admin" element={<Admin />} />
      </Routes>
      <Footer />
    </div>
  )
}
