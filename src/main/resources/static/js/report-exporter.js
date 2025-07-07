/**
 * Módulo para exportação de relatórios
 * Utiliza html2pdf.bundle.min.js para gerar PDFs e funcionalidades nativas para CSV
 */

/**
 * Exporta um elemento HTML para PDF
 * @param {string} elementId - ID do elemento HTML a ser exportado
 * @param {string} filename - Nome do arquivo (sem extensão)
 * @param {Object} options - Opções de configuração
 */
function exportToPdf(elementId, filename, options = {}) {
    // Verificar se o elemento existe
    const element = document.getElementById(elementId);
    if (!element) {
        console.error('Elemento não encontrado:', elementId);
        showNotification('Erro: Elemento não encontrado para exportação', 'error');
        return;
    }

    // Configurações padrão
    const defaultOptions = {
        margin: 1,
        filename: filename || 'relatorio.pdf',
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { 
            scale: 2,
            useCORS: true,
            allowTaint: true,
            scrollX: 0,
            scrollY: 0,
            width: element.scrollWidth,
            height: element.scrollHeight
        },
        jsPDF: { 
            unit: 'cm', 
            format: 'a4', 
            orientation: 'portrait' 
        }
    };

    // Mesclar opções fornecidas com padrões
    const config = Object.assign({}, defaultOptions, options);

    // Adicionar classe para estilização de impressão
    element.classList.add('printing');

    // Ocultar botões de exportação durante a geração
    const exportButtons = element.querySelectorAll('.export-button');
    exportButtons.forEach(btn => btn.style.display = 'none');

    // Mostrar indicador de carregamento
    showLoadingIndicator('Gerando PDF...');

    // Gerar PDF
    html2pdf()
        .from(element)
        .set(config)
        .save()
        .then(() => {
            // Remover classe de impressão
            element.classList.remove('printing');
            // Restaurar botões
            exportButtons.forEach(btn => btn.style.display = '');
            hideLoadingIndicator();
            showNotification('PDF gerado com sucesso!', 'success');
        })
        .catch((error) => {
            console.error('Erro ao gerar PDF:', error);
            // Remover classe de impressão
            element.classList.remove('printing');
            // Restaurar botões
            exportButtons.forEach(btn => btn.style.display = '');
            hideLoadingIndicator();
            showNotification('Erro ao gerar PDF: ' + error.message, 'error');
        });
}

/**
 * Exporta uma tabela para CSV
 * @param {string} tableId - ID da tabela HTML
 * @param {string} filename - Nome do arquivo (sem extensão)
 */
function exportTableToCSV(tableId, filename) {
    const table = document.getElementById(tableId);
    if (!table) {
        console.error('Tabela não encontrada:', tableId);
        showNotification('Erro: Tabela não encontrada para exportação', 'error');
        return;
    }

    // Mostrar indicador de carregamento
    showLoadingIndicator('Gerando CSV...');

    try {
        let csv = [];
        const rows = table.querySelectorAll('tr');

        // Processar cada linha
        rows.forEach(row => {
            const cells = row.querySelectorAll('td, th');
            const rowData = Array.from(cells).map(cell => {
                // Limpar o texto e escapar aspas
                let text = cell.textContent.trim();
                text = text.replace(/"/g, '""'); // Escapar aspas duplas
                // Envolver em aspas se contiver vírgula, quebra de linha ou aspas
                if (text.includes(',') || text.includes('\n') || text.includes('"')) {
                    text = '"' + text + '"';
                }
                return text;
            });
            csv.push(rowData.join(','));
        });

        // Criar e baixar o arquivo
        const csvContent = csv.join('\n');
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        
        if (link.download !== undefined) {
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', filename || 'relatorio.csv');
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }

        hideLoadingIndicator();
        showNotification('CSV gerado com sucesso!', 'success');
    } catch (error) {
        console.error('Erro ao gerar CSV:', error);
        hideLoadingIndicator();
        showNotification('Erro ao gerar CSV: ' + error.message, 'error');
    }
}

/**
 * Mostra um indicador de carregamento
 * @param {string} message - Mensagem a ser exibida
 */
function showLoadingIndicator(message = 'Processando...') {
    // Remover indicador existente se houver
    hideLoadingIndicator();

    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'loadingIndicator';
    loadingDiv.className = 'loading-indicator';
    loadingDiv.innerHTML = `
        <div class="loading-backdrop">
            <div class="loading-content">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Carregando...</span>
                </div>
                <p class="mt-2 mb-0">${message}</p>
            </div>
        </div>
    `;

    document.body.appendChild(loadingDiv);
}

/**
 * Oculta o indicador de carregamento
 */
function hideLoadingIndicator() {
    const loadingDiv = document.getElementById('loadingIndicator');
    if (loadingDiv) {
        loadingDiv.remove();
    }
}

/**
 * Mostra uma notificação temporária
 * @param {string} message - Mensagem a ser exibida
 * @param {string} type - Tipo da notificação ('success', 'error', 'warning', 'info')
 */
function showNotification(message, type = 'info') {
    // Criar elemento de notificação
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} notification-toast`;
    notification.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="bi bi-${getIconForType(type)} me-2"></i>
            <span>${message}</span>
        </div>
    `;

    // Adicionar ao corpo da página
    document.body.appendChild(notification);

    // Remover após 5 segundos
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

/**
 * Retorna o ícone apropriado para o tipo de notificação
 * @param {string} type - Tipo da notificação
 * @returns {string} - Classe do ícone
 */
function getIconForType(type) {
    switch (type) {
        case 'success':
            return 'check-circle';
        case 'error':
            return 'exclamation-circle';
        case 'warning':
            return 'exclamation-triangle';
        case 'info':
        default:
            return 'info-circle';
    }
}

/**
 * Configurações específicas para diferentes tipos de relatório
 */
const reportConfigs = {
    agendamentos: {
        jsPDF: { orientation: 'landscape' },
        margin: 0.5
    },
    pacientes: {
        jsPDF: { orientation: 'portrait' },
        margin: 1
    },
    desempenho: {
        jsPDF: { orientation: 'landscape' },
        margin: 0.5
    }
};

/**
 * Exporta relatório com configurações específicas
 * @param {string} reportType - Tipo do relatório
 * @param {string} elementId - ID do elemento a ser exportado
 * @param {string} filename - Nome do arquivo
 */
function exportReport(reportType, elementId, filename) {
    const config = reportConfigs[reportType] || {};
    exportToPdf(elementId, filename, config);
}

// Adicionar estilos CSS para os componentes de exportação
const styles = `
    .loading-indicator {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 9999;
    }

    .loading-backdrop {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .loading-content {
        background-color: white;
        padding: 2rem;
        border-radius: 0.5rem;
        text-align: center;
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
    }

    .notification-toast {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1050;
        min-width: 300px;
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
    }

    .notification-toast.show {
        opacity: 1;
        transform: translateX(0);
    }

    /* Estilos específicos para impressão */
    .printing .export-button {
        display: none !important;
    }

    .printing .breadcrumb {
        display: none !important;
    }

    .printing .btn {
        display: none !important;
    }

    .printing .card-header .btn {
        display: none !important;
    }

    @media print {
        .export-button,
        .breadcrumb,
        .btn {
            display: none !important;
        }
        
        .card {
            border: none !important;
            box-shadow: none !important;
        }
        
        .card-header {
            background-color: #007bff !important;
            -webkit-print-color-adjust: exact;
        }
    }
`;

// Adicionar estilos ao documento
const styleSheet = document.createElement('style');
styleSheet.textContent = styles;
document.head.appendChild(styleSheet);

// Animar notificações quando criadas
document.addEventListener('DOMContentLoaded', function() {
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            mutation.addedNodes.forEach(function(node) {
                if (node.nodeType === 1 && node.classList.contains('notification-toast')) {
                    setTimeout(() => {
                        node.classList.add('show');
                    }, 100);
                }
            });
        });
    });

    observer.observe(document.body, {
        childList: true
    });
});
