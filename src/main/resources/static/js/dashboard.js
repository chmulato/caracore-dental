/**
 * Script para a página de Dashboard
 */
document.addEventListener('DOMContentLoaded', function() {
    // Função para animar os números nos cards de estatísticas
    function animateStats() {
        document.querySelectorAll('.card .h3').forEach(function(element) {
            const finalValue = parseInt(element.textContent);
            let currentValue = 0;
            const duration = 1500; // duração da animação em ms
            const frameDuration = 1000 / 60; // 60fps
            const totalFrames = Math.round(duration / frameDuration);
            
            let frame = 0;
            const counter = setInterval(() => {
                frame++;
                const progress = frame / totalFrames;
                currentValue = Math.floor(progress * finalValue);
                if (frame === totalFrames) {
                    clearInterval(counter);
                    currentValue = finalValue;
                }
                element.textContent = currentValue;
            }, frameDuration);
        });
    }
    
    // Iniciar a animação quando a página carregar
    animateStats();
    
    // Adicionar efeito de hover para a tabela e funcionalidade de clique
    const tableRows = document.querySelectorAll('.dashboard-table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('mouseover', () => {
            row.style.backgroundColor = 'rgba(0, 123, 255, 0.05)';
            row.style.cursor = 'pointer';
        });
        
        row.addEventListener('mouseout', () => {
            row.style.backgroundColor = '';
            row.style.cursor = 'default';
        });
        
        // Adicionar clique para visualizar detalhes (se necessário)
        row.addEventListener('click', () => {
            // Pode ser implementada navegação para detalhes da consulta
            // Exemplo: window.location.href = '/consultas/detalhes/' + id;
            console.log('Clique na linha da tabela');
        });
    });
    
    // Inicialização para tooltips do Bootstrap se existirem
    if (typeof bootstrap !== 'undefined') {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
});
