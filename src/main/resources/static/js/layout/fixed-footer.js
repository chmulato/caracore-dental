/**
 * Script para o Footer Fixo
 */
document.addEventListener('DOMContentLoaded', function() {
    // Referência ao footer fixo e body
    const fixedFooter = document.querySelector('.footer-fixed');
    const body = document.body;
    
    // Função para ajustar o footer quando a sidebar é aberta/fechada
    function adjustFooter() {
        if (fixedFooter) {
            if (window.innerWidth > 991) {
                // Em desktop, o posicionamento é gerenciado pelo CSS
                // (Removemos inline styles para permitir que o CSS funcione)
                fixedFooter.style.left = '';
            } else {
                // Em dispositivos móveis
                fixedFooter.style.left = '0';
            }
        }
    }
    
    // Observador para mudanças na classe do body (quando sidebar é aberto/fechado)
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.attributeName === 'class') {
                adjustFooter();
            }
        });
    });
    
    // Iniciar observação
    if (body && fixedFooter) {
        observer.observe(body, { attributes: true });
        
        // Ajustar inicialmente
        adjustFooter();
        
        // Ajustar em mudanças de tamanho da tela
        window.addEventListener('resize', adjustFooter);
    }
});
