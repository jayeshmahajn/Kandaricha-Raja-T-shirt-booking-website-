const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function handle(res) {
  const body = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error(body.message || `Request failed (${res.status})`);
  }
  return body;
}

export const api = {
  submitBooking: (data) =>
    fetch(`${BASE_URL}/api/bookings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }).then(handle),

  listDesigns: () =>
    fetch(`${BASE_URL}/api/designs`).then(handle),

  imageUrl: (fileName) => {
    if (fileName.startsWith('http')) return fileName;
    return `${BASE_URL}/uploads/${fileName}`;
  },

  adminLogin: (key) =>
    fetch(`${BASE_URL}/api/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ key }),
    }).then(handle),

  getBookings: (key) =>
    fetch(`${BASE_URL}/api/bookings`, {
      headers: { 'X-Admin-Key': key },
    }).then(handle),

  downloadExcelUrl: () => `${BASE_URL}/api/bookings/download`,

  uploadDesign: (key, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return fetch(`${BASE_URL}/api/designs`, {
      method: 'POST',
      headers: { 'X-Admin-Key': key },
      body: formData,
    }).then(handle);
  },

  deleteDesign: (key, url) =>
    fetch(`${BASE_URL}/api/designs?url=${encodeURIComponent(url)}`, {
      method: 'DELETE',
      headers: { 'X-Admin-Key': key },
    }).then(handle),
};

export { BASE_URL };
