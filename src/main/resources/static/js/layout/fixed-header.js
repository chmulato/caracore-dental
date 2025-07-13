/**
 * Script para o Header Fixo
 */
document.addEventListener('DOMContentLoaded', function() {
    // Referência ao header fixo e body
    const headerFixed = document.querySelector('.header-fixed');
    const body = document.body;
    
    // Função para ajustar o header quando a sidebar é aberta/fechada
    function adjustHeader() {
        if (headerFixed) {
            if (window.innerWidth > 991) {
                // Em desktop, o posicionamento é gerenciado pelo CSS
                // (Removemos inline styles para permitir que o CSS funcione)
                headerFixed.style.left = '';
            } else {
                // Em dispositivos móveis
                headerFixed.style.left = '0';
            }
        }
    }
    
    // Observador para mudanças na classe do body (quando sidebar é aberto/fechado)
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.attributeName === 'class') {
                adjustHeader();
            }
        });
    });
    
    // Iniciar observação
    if (body && headerFixed) {
        observer.observe(body, { attributes: true });
        
        // Ajustar inicialmente
        adjustHeader();
        
        // Ajustar em mudanças de tamanho da tela
        window.addEventListener('resize', adjustHeader);
    }
});
