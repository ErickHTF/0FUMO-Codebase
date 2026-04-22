const API_BASE = '/api';

function getToken() {
  return localStorage.getItem('token');
}

function saveSession(data) {
  localStorage.setItem('token', data.token);
  localStorage.setItem('user', JSON.stringify(data.user));
}

function clearSession() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
}

function getUser() {
  const raw = localStorage.getItem('user');
  return raw ? JSON.parse(raw) : null;
}

async function apiFetch(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) headers['Authorization'] = 'Bearer ' + token;

  const res = await fetch(API_BASE + path, { ...options, headers });
  const body = await res.json().catch(() => ({}));

  if (!res.ok) throw { status: res.status, body };
  return body;
}

const Auth = {
  register: (name, email, password) =>
    apiFetch('/auth/register', { method: 'POST', body: JSON.stringify({ name, email, password }) }),

  login: (email, password) =>
    apiFetch('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
};

const Users = {
  me: () => apiFetch('/users/me'),
  update: (id, name, email) =>
    apiFetch(`/users/${id}`, { method: 'PUT', body: JSON.stringify({ name, email }) }),
  delete: (id) =>
    apiFetch(`/users/${id}`, { method: 'DELETE' }),
};
