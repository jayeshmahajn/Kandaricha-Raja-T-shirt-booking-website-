import { Link } from 'react-router-dom'
import { MANDAL } from '../config.js'

export default function Navbar() {
  return (
    <header className="navbar">
      <Link to="/" className="navbar-brand">
        <img src="/mandal-logo.jpg" alt="Mandal Logo" className="navbar-logo" />
        <span className="navbar-names">
          <span className="navbar-name-mr">{MANDAL.nameMarathi}</span>
          <span className="navbar-name-en">{MANDAL.nameEnglish} · Est. {MANDAL.since}</span>
        </span>
      </Link>
      <nav className="navbar-links">
        <a href="#book">बुकिंग</a>
        <a href="#designs">डिझाईन्स</a>
        <a href="#contact">संपर्क</a>
      </nav>
    </header>
  )
}
