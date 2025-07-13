/**
 * Sidebar Navigation - Funcionalidades do menu lateral
 */
document.addEventListener('DOMContentLoaded', function() {
    // Elementos da sidebar
    const body = document.body;
    const sidebar = document.querySelector('.sidebar');
    
    // Checar se devemos fechar a sidebar em desktop (de acordo com a preferência salva)
    if (window.innerWidth > 991) {
        // Se houver uma preferência salva no localStorage, use-a
        const sidebarState = localStorage.getItem('sidebarClosed');
        // Se a preferência for 'true', feche a sidebar
        if (sidebarState === 'true') {
            body.classList.add('sidebar-closed');
        }
    }
    
    // Toggle para abrir/fechar a sidebar
    function toggleSidebar() {
        // Em mobile
        if (window.innerWidth <= 991) {
            body.classList.toggle('sidebar-open');
        } 
        // Em desktop
        else {
            const isClosed = body.classList.toggle('sidebar-closed');
            // Salva o estado da sidebar no localStorage
            localStorage.setItem('sidebarClosed', isClosed);
        }
    }
    
    // Criar overlay para dispositivos móveis se ainda não existir
    let sidebarOverlay = document.querySelector('.sidebar-overlay');
    if (!sidebarOverlay) {
        sidebarOverlay = document.createElement('div');
        sidebarOverlay.classList.add('sidebar-overlay');
        body.appendChild(sidebarOverlay);
    }
    
    // Listener para o botão toggle em dispositivos móveis
    const mobileToggle = document.getElementById('mobile-toggle');
    if (mobileToggle) {
        mobileToggle.addEventListener('click', toggleSidebar);
    }
    
    // Fecha a sidebar ao clicar no overlay (apenas mobile)
    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function() {
            if (window.innerWidth <= 991) {
                body.classList.remove('sidebar-open');
            }
        });
    }
    
    // Gerenciamento de submenus
    const submenuToggles = document.querySelectorAll('[data-bs-toggle="collapse"]');
    // Fecha a sidebar em dispositivos móveis ao clicar em links
    const sidebarLinks = document.querySelectorAll('.sidebar .nav-link');
    sidebarLinks.forEach(link => {
        link.addEventListener('click', function() {
            if (window.innerWidth <= 991) {
                body.classList.remove('sidebar-open');
            }
        });
    });
    
    // Adapta a sidebar quando a janela é redimensionada
    window.addEventListener('resize', function() {
        if (window.innerWidth <= 991) {
            // Em dispositivos móveis, fechamos a sidebar por padrão
            body.classList.remove('sidebar-open');
            // Removemos a classe de desktop para evitar conflitos
            body.classList.remove('sidebar-closed');
        } else {
            // Em desktop, restauramos o estado salvo
            const sidebarClosed = localStorage.getItem('sidebarClosed');
            // Se a preferência for 'true', mantenha a sidebar fechada
            if (sidebarClosed === 'true') {
                body.classList.add('sidebar-closed');
            } else {
                body.classList.remove('sidebar-closed');
            }
            // Removemos a classe de mobile para evitar conflitos
            body.classList.remove('sidebar-open');
        }
    });
});
