import { Link } from 'react-router-dom'
import { MANDAL } from '../config.js'

export default function Footer() {
  return (
    <footer className="footer" id="contact">
      <div className="footer-inner">
        <div>
          <div className="footer-mark">गणपती बाप्पा मोरया 🙏</div>
          <p className="footer-address">{MANDAL.address}</p>
        </div>
        <div className="footer-contact">
          <a href={`tel:${MANDAL.contactPhone.replace(/\s/g, '')}`}>Contact Ganesh R patil : {MANDAL.contactPhone}</a>
          <a href={MANDAL.instagram} target="_blank" rel="noreferrer">Instagram वर पहा →</a>
          <Link to="/admin" className="footer-admin-link">Admin</Link>
        </div>
      </div>
      <p className="footer-note">या वर्षीच्या टी-शर्ट बुकिंगसाठी बनवलेली अधिकृत वेबसाईट.</p>
    </footer>
  )
}
