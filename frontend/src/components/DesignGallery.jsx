import { useEffect, useState } from 'react'
import { api } from '../api/api.js'

export default function DesignGallery() {
  const [images, setImages] = useState([])
  const [status, setStatus] = useState('loading')
  const [fullscreenImage, setFullscreenImage] = useState(null)

  useEffect(() => {
    api.listDesigns()
      .then((res) => {
        setImages(res.data || [])
        setStatus('ready')
      })
      .catch(() => setStatus('error'))
  }, [])

  // Close modal on Escape key
  useEffect(() => {
    function handleKeyDown(e) {
      if (e.key === 'Escape') setFullscreenImage(null)
    }
    if (fullscreenImage) window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [fullscreenImage])

  if (status === 'loading') return <p className="muted">डिझाईन्स लोड होत आहेत...</p>
  if (status === 'error') return <p className="muted">डिझाईन्स आणता आली नाहीत.</p>
  if (images.length === 0) {
    return <p className="muted">अजून कोणतेही डिझाईन अपलोड केलेले नाही. लवकरच इथे दिसेल!</p>
  }

  return (
    <>
      <div className="pinboard">
        {images.map((img, i) => (
          <div className="pin-photo" key={img} style={{ transform: `rotate(${(i % 5 - 2) * 2.4}deg)` }}>
            <span className="pin" aria-hidden="true" />
            <img 
              src={api.imageUrl(img)} 
              alt={`T-shirt design ${i + 1}`} 
              loading="lazy" 
              onClick={() => setFullscreenImage(img)}
              style={{ cursor: 'pointer' }}
            />
          </div>
        ))}
      </div>

      {fullscreenImage && (
        <div className="fullscreen-modal" onClick={() => setFullscreenImage(null)}>
          <div className="fullscreen-modal-close">✕</div>
          <img 
            src={api.imageUrl(fullscreenImage)} 
            alt="Fullscreen T-shirt design" 
            onClick={(e) => e.stopPropagation()} 
          />
        </div>
      )}
    </>
  )
}
