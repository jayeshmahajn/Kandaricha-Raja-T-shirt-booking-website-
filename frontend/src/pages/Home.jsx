import Toran from '../components/Toran.jsx'
import BookingForm from '../components/BookingForm.jsx'
import DesignGallery from '../components/DesignGallery.jsx'
import { MANDAL, SIZE_CHART } from '../config.js'

export default function Home() {
  return (
    <main>
      <section className="hero">
        <Toran />
        <div className="hero-content">
          <p className="hero-eyebrow">गणपती बाप्पा मोरया</p>
          <h1>{MANDAL.tagline}</h1>
          <p className="hero-sub">{MANDAL.subTagline}</p>
          <a href="#book" className="btn-primary hero-cta">आत्ताच बुक करा</a>
        </div>
      </section>

      <section className="section" id="designs">
        <div className="section-head">
          <h2>यंदाची डिझाईन्स</h2>
          <p>मंडळाने निवडलेले टी-शर्ट डिझाईन्स - बुकिंग करण्याआधी पहा.</p>
        </div>
        <DesignGallery />
      </section>

      <section className="section section-alt" id="book">
        <div className="section-head">
          <h2>टी-शर्ट बुकिंग फॉर्म</h2>
          <p>खाली माहिती भरा - तुमचे बुकिंग मंडळाच्या नोंदवहीत लगेच सेव्ह होईल.</p>
        </div>

        <div className="book-layout">
          <BookingForm />

          <aside className="size-chart-card">
            <h3>साईज चार्ट</h3>
            <table>
              <thead>
                <tr><th>साईज</th><th>तपशील</th></tr>
              </thead>
              <tbody>
                {SIZE_CHART.map((s) => (
                  <tr key={s.size}><td>{s.size}</td><td>{s.chest}</td></tr>
                ))}
              </tbody>
            </table>
            <p className="size-note">खात्री नसल्यास घरातील योग्य मापाच्या शर्टाचा चेस्ट इंचात माप घ्या.</p>
          </aside>
        </div>
      </section>
    </main>
  )
}
