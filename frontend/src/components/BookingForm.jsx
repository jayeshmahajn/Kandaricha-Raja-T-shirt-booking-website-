import { useState } from 'react'
import { api } from '../api/api.js'
import { SIZE_CHART } from '../config.js'

const initialForm = { fullName: '', size: '', sleeveType: '', phoneNumber: '' }

export default function BookingForm() {
  const [form, setForm] = useState(initialForm)
  const [errors, setErrors] = useState({})
  const [status, setStatus] = useState('idle') // idle | submitting | success | error
  const [serverMessage, setServerMessage] = useState('')
  const [tokenNo, setTokenNo] = useState(null)

  function validate() {
    const next = {}
    if (!form.fullName.trim()) next.fullName = 'नाव टाका'
    if (!form.size) next.size = 'साईज निवडा'
    if (!form.sleeveType) next.sleeveType = 'स्लीव्ह प्रकार निवडा'
    if (!/^\d{10}$/.test(form.phoneNumber.trim())) next.phoneNumber = '10 अंकी मोबाईल नंबर टाका'
    setErrors(next)
    return Object.keys(next).length === 0
  }

  async function handleSubmit(e) {
    e.preventDefault()
    if (!validate()) return

    setStatus('submitting')
    setServerMessage('')
    try {
      await api.submitBooking(form)
      setTokenNo(Math.floor(1000 + Math.random() * 9000))
      setStatus('success')
      setForm(initialForm)
    } catch (err) {
      setStatus('error')
      setServerMessage(err.message || 'काहीतरी चूक झाली, पुन्हा प्रयत्न करा.')
    }
  }

  if (status === 'success') {
    return (
      <div className="token-card" role="status">
        <div className="token-perforation" />
        <h3>बुकिंग यशस्वी! 🎉</h3>
        <p>तुमची नोंदणी झाली आहे. मंडळाकडून टी-शर्ट वाटपाची माहिती फोनवर कळवली जाईल.</p>
        <div className="token-number">Token #{tokenNo}</div>
        <button className="btn-secondary" onClick={() => setStatus('idle')}>
          आणखी एक बुकिंग करा
        </button>
      </div>
    )
  }

  return (
    <form className="booking-form" onSubmit={handleSubmit} noValidate>
      <div className="form-field">
        <label htmlFor="fullName">पूर्ण नाव</label>
        <input
          id="fullName"
          type="text"
          placeholder="उदा. राहुल शिंदे"
          value={form.fullName}
          onChange={(e) => setForm({ ...form, fullName: e.target.value })}
        />
        {errors.fullName && <span className="field-error">{errors.fullName}</span>}
      </div>

      <div className="form-field">
        <label htmlFor="size">टी-शर्ट साईज (चेस्ट इंच)</label>
        <select
          id="size"
          value={form.size}
          onChange={(e) => setForm({ ...form, size: e.target.value })}
        >
          <option value="">साईज निवडा</option>
          {SIZE_CHART.map((s) => (
            <option key={s.size} value={s.size}>{s.size} — {s.chest}</option>
          ))}
        </select>
        {errors.size && <span className="field-error">{errors.size}</span>}
      </div>

      <div className="form-field">
        <label htmlFor="sleeveType">स्लीव्ह प्रकार (Sleeve Type)</label>
        <select
          id="sleeveType"
          value={form.sleeveType}
          onChange={(e) => setForm({ ...form, sleeveType: e.target.value })}
        >
          <option value="">स्लीव्ह प्रकार निवडा</option>
          <option value="Half Sleeve">Half Sleeve (हाफ स्लीव्ह)</option>
          <option value="Full Sleeve">Full Sleeve (फुल स्लीव्ह)</option>
        </select>
        {errors.sleeveType && <span className="field-error">{errors.sleeveType}</span>}
      </div>

      <div className="form-field">
        <label htmlFor="phoneNumber">मोबाईल नंबर</label>
        <input
          id="phoneNumber"
          type="tel"
          inputMode="numeric"
          maxLength={10}
          placeholder="9876543210"
          value={form.phoneNumber}
          onChange={(e) => setForm({ ...form, phoneNumber: e.target.value.replace(/\D/g, '') })}
        />
        {errors.phoneNumber && <span className="field-error">{errors.phoneNumber}</span>}
      </div>

      {status === 'error' && <div className="form-error-banner">{serverMessage}</div>}

      <div style={{ fontSize: '13px', color: 'var(--vermillion-dark)', fontWeight: '600', textAlign: 'center', marginTop: '10px', marginBottom: '10px' }}>
        टीप: पेमेंट करण्यापूर्वी कृपया मंडळाशी संपर्क साधा. <br /> (Please contact the mandal before making any payment)
      </div>

      <button className="btn-primary" type="submit" disabled={status === 'submitting'}>
        {status === 'submitting' ? 'पाठवत आहे...' : 'बुकिंग कन्फर्म करा'}
      </button>
    </form>
  )
}
