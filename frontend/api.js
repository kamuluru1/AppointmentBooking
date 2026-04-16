/**
 * api.js — All HTTP calls to the Spring Boot backend.
 * Base URL: http://localhost:8080
 * Auth: session cookie (credentials: 'include')
 */

const BASE = 'http://127.0.0.1:8080';

const api = {

  // ── AUTH ──────────────────────────────────────────────
  async login(email, password) {
    return request('POST', '/api/auth/login', { email, password });
  },

  async register(name, email, password, role) {
    return request('POST', '/api/auth/register', { name, email, password, role });
  },

  async logout() {
    return request('POST', '/api/auth/logout');
  },

  // ── ROOMS ─────────────────────────────────────────────
  async getRooms() {
    return request('GET', '/api/rooms');
  },

  async createRoom(room) {
    return request('POST', '/api/rooms', room);
  },

  async updateRoom(id, room) {
    return request('PUT', `/api/rooms/${id}`, room);
  },

  async deleteRoom(id) {
    return request('DELETE', `/api/rooms/${id}`);
  },

  // ── BOOKINGS ──────────────────────────────────────────
  async getAllBookings() {
    return request('GET', '/api/bookings');
  },

  async getUserBookings(userId) {
    return request('GET', `/api/bookings/user/${userId}`);
  },

  async createBooking(userId, roomId, startTime, endTime) {
    return request('POST', '/api/bookings', { userId, roomId, startTime, endTime });
  },

  async cancelBooking(bookingId, userId) {
    return request('PUT', `/api/bookings/${bookingId}/cancel?userId=${userId}`);
  },

  // ── ADMIN ─────────────────────────────────────────────
  async getAnalytics() {
    return request('GET', '/api/admin/analytics');
  },
};

async function request(method, path, body) {
  const opts = {
    method,
    credentials: 'include',        // Send session cookie
    headers: { 'Content-Type': 'application/json' },
  };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(BASE + path, opts);

  // Redirect to login on 401
  if (res.status === 401 || res.status === 403) {
    if (!path.includes('/api/auth/')) {
      sessionStorage.clear();
      window.location.href = 'login.html';
    }
    throw new Error('Unauthorized');
  }

  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  if (!res.ok) {
    throw new Error(typeof data === 'string' ? data : (data?.message || 'Request failed'));
  }
  return data;
}

// ── Toast helper (available globally) ─────────────────
function showToast(message, type = 'info') {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  const icons = { success: '✓', error: '✕', info: 'ℹ' };
  toast.innerHTML = `<span>${icons[type] || ''}</span><span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// ── Auth guard: call on every protected page ───────────
function requireAuth() {
  if (!sessionStorage.getItem('userId')) {
    window.location.href = 'login.html';
    return false;
  }
  return true;
}

function isAdmin() {
  return sessionStorage.getItem('userRole') === 'ROLE_ADMIN';
}

function currentUserId() {
  return parseInt(sessionStorage.getItem('userId'), 10);
}

function currentUserName() {
  return sessionStorage.getItem('userName') || 'User';
}
