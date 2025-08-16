// Toggle de tema claro/escuro com persistência em localStorage
// Aplica classe em documentElement (root) e também no body para compatibilidade com seletores existentes.

document.addEventListener('DOMContentLoaded', () => {
  const btn = document.getElementById('themeToggleBtn');
  const icon = document.getElementById('themeToggleIcon');
  if(!btn || !icon) return;

  function applyIcon() {
    const dark = document.documentElement.classList.contains('dark-mode');
    icon.className = dark ? 'bi bi-sun-fill' : 'bi bi-moon-stars';
    btn.setAttribute('aria-pressed', dark ? 'true' : 'false');
    btn.title = dark ? 'Tema claro' : 'Tema escuro';
  }

  btn.addEventListener('click', () => {
    document.documentElement.classList.toggle('dark-mode');
    document.body.classList.toggle('dark-mode');
    const isDark = document.documentElement.classList.contains('dark-mode');
    try { localStorage.setItem('cca-theme', isDark ? 'dark' : 'light'); } catch(e) { /* ignore */ }
    applyIcon();
  });

  applyIcon();
});
