import { useEffect, useState } from 'react'
import { api } from '../api/api.js'

const STORAGE_KEY = 'mandal_admin_key'

export default function Admin() {
  const [key, setKey] = useState(() => sessionStorage.getItem(STORAGE_KEY) || '')
  const [authed, setAuthed] = useState(false)
  const [loginInput, setLoginInput] = useState('')
  const [loginError, setLoginError] = useState('')

  useEffect(() => {
    if (key) verifyAndEnter(key)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function verifyAndEnter(candidateKey) {
    try {
      await api.adminLogin(candidateKey)
      sessionStorage.setItem(STORAGE_KEY, candidateKey)
      setKey(candidateKey)
      setAuthed(true)
    } catch {
      sessionStorage.removeItem(STORAGE_KEY)
      setAuthed(false)
    }
  }

  async function handleLogin(e) {
    e.preventDefault()
    setLoginError('')
    try {
      await api.adminLogin(loginInput)
      sessionStorage.setItem(STORAGE_KEY, loginInput)
      setKey(loginInput)
      setAuthed(true)
    } catch (err) {
      setLoginError(err.message || 'चुकीचा पासवर्ड')
    }
  }

  function logout() {
    sessionStorage.removeItem(STORAGE_KEY)
    setKey('')
    setAuthed(false)
    setLoginInput('')
  }

  if (!authed) {
    return (
      <main className="admin-gate">
        <form className="admin-login-card" onSubmit={handleLogin}>
          <h2>कार्यकर्ता लॉगिन</h2>
          <p className="muted">फक्त अधिकृत कार्यकर्त्यांसाठी - डिझाईन अपलोड व बुकिंग यादी.</p>
          <input
            type="password"
            placeholder="Admin password"
            value={loginInput}
            onChange={(e) => setLoginInput(e.target.value)}
            autoFocus
          />
          {loginError && <span className="field-error">{loginError}</span>}
          <button className="btn-primary" type="submit">लॉगिन</button>
        </form>
      </main>
    )
  }

  return <AdminDashboard adminKey={key} onLogout={logout} />
}

function AdminDashboard({ adminKey, onLogout }) {
  const [bookings, setBookings] = useState([])
  const [images, setImages] = useState([])
  const [loading, setLoading] = useState(true)
  const [uploadStatus, setUploadStatus] = useState('idle')

  async function refresh() {
    setLoading(true)
    const [bookingsRes, imagesRes] = await Promise.all([
      api.getBookings(adminKey),
      api.listDesigns(),
    ])
    setBookings(bookingsRes.data || [])
    setImages(imagesRes.data || [])
    setLoading(false)
  }

  useEffect(() => { refresh() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  async function handleUpload(e) {
    const file = e.target.files[0]
    if (!file) return
    setUploadStatus('uploading')
    try {
      await api.uploadDesign(adminKey, file)
      setUploadStatus('idle')
      e.target.value = ''
      refresh()
    } catch (err) {
      setUploadStatus('error')
      alert(err.message || 'अपलोड फेल झाले')
    }
  }

  async function handleDelete(fileName) {
    if (!confirm('हे डिझाईन डिलीट करायचे?')) return
    await api.deleteDesign(adminKey, fileName)
    refresh()
  }

  return (
    <main className="admin-dashboard">
      <div className="admin-header">
        <h2>कार्यकर्ता डॅशबोर्ड</h2>
        <div className="admin-header-actions">
          <button className="btn-secondary" onClick={() => {
            fetch(api.downloadExcelUrl(), { headers: { 'X-Admin-Key': adminKey } })
              .then((r) => r.blob())
              .then((blob) => {
                const url = URL.createObjectURL(blob)
                const a = document.createElement('a')
                a.href = url
                a.download = 'bookings.xlsx'
                a.click()
                URL.revokeObjectURL(url)
              })
          }}>Excel डाउनलोड करा</button>
          <button className="btn-secondary" onClick={onLogout}>लॉगआऊट</button>
        </div>
      </div>

      <section className="admin-section">
        <h3>डिझाईन अपलोड करा</h3>
        <label className="upload-drop">
          <input type="file" accept="image/*" onChange={handleUpload} hidden />
          {uploadStatus === 'uploading' ? 'अपलोड होत आहे...' : '+ नवीन टी-शर्ट फोटो निवडा'}
        </label>

        <div className="admin-image-grid">
          {images.map((img) => (
            <div className="admin-image-card" key={img}>
              <img src={api.imageUrl(img)} alt={img} />
              <button onClick={() => handleDelete(img)}>काढून टाका</button>
            </div>
          ))}
        </div>
      </section>

      <section className="admin-section">
        <h3>बुकिंग यादी ({bookings.length})</h3>
        {loading ? (
          <p className="muted">लोड होत आहे...</p>
        ) : bookings.length === 0 ? (
          <p className="muted">अजून कोणतीही बुकिंग नाही.</p>
        ) : (
          <div className="table-wrap">
            <table className="bookings-table">
              <thead>
                <tr>
                  {Object.keys(bookings[0]).filter(h => h !== 'id').map((h) => <th key={h}>{h}</th>)}
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {bookings.map((b, i) => (
                  <tr key={b.id || i}>
                    {Object.entries(b).filter(([k]) => k !== 'id').map(([k, v], j) => <td key={j}>{v}</td>)}
                    <td>
                      <button onClick={async () => {
                        if (!confirm('हे बुकिंग डिलीट करायचे?')) return;
                        await api.deleteBooking(adminKey, b.id);
                        refresh();
                      }}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </main>
  )
}
