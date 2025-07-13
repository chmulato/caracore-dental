/**
 * Header Public - Funcionalidades do cabeçalho para agenda pública
 */
document.addEventListener('DOMContentLoaded', function() {
    // Detectar scroll para aplicar efeito no header
    const publicNavbar = document.querySelector('.public-header .navbar');
    
    if (publicNavbar) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 10) {
                publicNavbar.classList.add('scrolled');
            } else {
                publicNavbar.classList.remove('scrolled');
            }
        });
        
        // Verificar posição inicial de scroll
        if (window.scrollY > 10) {
            publicNavbar.classList.add('scrolled');
        }
    }
    
    // Inicializar dropdowns e tooltips
    const dropdownElements = document.querySelectorAll('.dropdown-toggle');
    if (typeof bootstrap !== 'undefined') {
        dropdownElements.forEach(function(element) {
            new bootstrap.Dropdown(element);
        });
        
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
});
