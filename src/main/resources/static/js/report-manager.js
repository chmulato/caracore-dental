/**
 * Funcionalidades avançadas para relatórios
 * Extensões para export, filtros e interatividade
 */

class ReportManager {
    constructor() {
        this.loadingElement = null;
        this.notifications = [];
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadSavedFilters();
    }

    setupEventListeners() {
        // Configurar listeners para filtros dinâmicos
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('report-filter')) {
                this.applyFilters();
            }
        });

        // Configurar listeners para botões de exportação
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('export-pdf-btn')) {
                this.exportToPDF(e.target.dataset.reportType);
            }
            if (e.target.classList.contains('export-csv-btn')) {
                this.exportToCSV(e.target.dataset.tableId);
            }
        });
    }

    /**
     * Exporta relatório para PDF com configurações otimizadas
     */
    exportToPDF(reportType) {
        const element = document.getElementById('relatorioContent');
        if (!element) {
            this.showNotification('Conteúdo do relatório não encontrado', 'error');
            return;
        }

        const configs = {
            agendamentos: {
                filename: `relatorio-agendamentos-${this.getCurrentDate()}.pdf`,
                options: {
                    margin: 0.5,
                    jsPDF: { orientation: 'landscape', format: 'a4' },
                    html2canvas: { scale: 2, useCORS: true }
                }
            },
            pacientes: {
                filename: `relatorio-pacientes-${this.getCurrentDate()}.pdf`,
                options: {
                    margin: 1,
                    jsPDF: { orientation: 'portrait', format: 'a4' },
                    html2canvas: { scale: 2, useCORS: true }
                }
            },
            desempenho: {
                filename: `relatorio-desempenho-${this.getCurrentDate()}.pdf`,
                options: {
                    margin: 0.5,
                    jsPDF: { orientation: 'landscape', format: 'a4' },
                    html2canvas: { scale: 2, useCORS: true }
                }
            }
        };

        const config = configs[reportType] || configs.agendamentos;
        this.generatePDF(element, config.filename, config.options);
    }

    /**
     * Gera PDF com tratamento de erros aprimorado
     */
    generatePDF(element, filename, options) {
        this.showLoading('Gerando PDF...');
        
        // Preparar elemento para exportação
        this.prepareElementForExport(element);

        html2pdf()
            .from(element)
            .set(options)
            .toPdf()
            .get('pdf')
            .then((pdf) => {
                // Adicionar metadados ao PDF
                pdf.setProperties({
                    title: `Relatório - ${filename}`,
                    subject: 'Relatório gerado pelo sistema Cara Core',
                    author: 'Sistema Cara Core',
                    creator: 'Sistema Cara Core CCA',
                    producer: 'html2pdf.js'
                });
                
                return pdf;
            })
            .save(filename)
            .then(() => {
                this.hideLoading();
                this.restoreElementAfterExport(element);
                this.showNotification('PDF gerado com sucesso!', 'success');
            })
            .catch((error) => {
                console.error('Erro ao gerar PDF:', error);
                this.hideLoading();
                this.restoreElementAfterExport(element);
                this.showNotification('Erro ao gerar PDF: ' + error.message, 'error');
            });
    }

    /**
     * Prepara elemento para exportação
     */
    prepareElementForExport(element) {
        element.classList.add('printing');
        
        // Ocultar elementos não necessários
        const hideElements = element.querySelectorAll('.no-print, .export-button, .btn:not(.badge)');
        hideElements.forEach(el => {
            el.dataset.originalDisplay = el.style.display;
            el.style.display = 'none';
        });

        // Ajustar tabelas para impressão
        const tables = element.querySelectorAll('.table-responsive');
        tables.forEach(table => {
            table.style.overflow = 'visible';
        });
    }

    /**
     * Restaura elemento após exportação
     */
    restoreElementAfterExport(element) {
        element.classList.remove('printing');
        
        // Restaurar elementos
        const hideElements = element.querySelectorAll('.no-print, .export-button, .btn:not(.badge)');
        hideElements.forEach(el => {
            el.style.display = el.dataset.originalDisplay || '';
            delete el.dataset.originalDisplay;
        });

        // Restaurar tabelas
        const tables = element.querySelectorAll('.table-responsive');
        tables.forEach(table => {
            table.style.overflow = '';
        });
    }

    /**
     * Exporta tabela para CSV com formatação melhorada
     */
    exportToCSV(tableId) {
        const table = document.getElementById(tableId);
        if (!table) {
            this.showNotification('Tabela não encontrada', 'error');
            return;
        }

        this.showLoading('Gerando CSV...');

        try {
            const csv = this.tableToCSV(table);
            const filename = `${tableId}-${this.getCurrentDate()}.csv`;
            this.downloadCSV(csv, filename);
            
            this.hideLoading();
            this.showNotification('CSV gerado com sucesso!', 'success');
        } catch (error) {
            console.error('Erro ao gerar CSV:', error);
            this.hideLoading();
            this.showNotification('Erro ao gerar CSV: ' + error.message, 'error');
        }
    }

    /**
     * Converte tabela para CSV
     */
    tableToCSV(table) {
        const rows = Array.from(table.querySelectorAll('tr'));
        const csvRows = [];

        rows.forEach(row => {
            const cells = Array.from(row.querySelectorAll('td, th'));
            const csvRow = cells.map(cell => {
                // Extrair texto limpo da célula
                let text = cell.textContent.trim();
                
                // Remover elementos de ação (botões)
                const actionElements = cell.querySelectorAll('button, a.btn, .btn');
                actionElements.forEach(el => {
                    text = text.replace(el.textContent.trim(), '');
                });
                
                text = text.trim();
                
                // Escapar aspas duplas
                text = text.replace(/"/g, '""');
                
                // Envolver em aspas se necessário
                if (text.includes(',') || text.includes('\n') || text.includes('"')) {
                    text = `"${text}"`;
                }
                
                return text;
            });
            
            csvRows.push(csvRow.join(','));
        });

        return csvRows.join('\n');
    }

    /**
     * Baixa arquivo CSV
     */
    downloadCSV(csvContent, filename) {
        const blob = new Blob(['\ufeff' + csvContent], { 
            type: 'text/csv;charset=utf-8;' 
        });
        
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        URL.revokeObjectURL(url);
    }

    /**
     * Aplica filtros dinâmicos
     */
    applyFilters() {
        const filters = this.getActiveFilters();
        const tables = document.querySelectorAll('table[data-filterable]');
        
        tables.forEach(table => {
            this.filterTable(table, filters);
        });
    }

    /**
     * Obtém filtros ativos
     */
    getActiveFilters() {
        const filters = {};
        const filterElements = document.querySelectorAll('.report-filter');
        
        filterElements.forEach(filter => {
            const name = filter.name;
            const value = filter.value;
            
            if (value && value !== '') {
                filters[name] = value;
            }
        });
        
        return filters;
    }

    /**
     * Filtra tabela
     */
    filterTable(table, filters) {
        const rows = table.querySelectorAll('tbody tr');
        let visibleCount = 0;
        
        rows.forEach(row => {
            let shouldShow = true;
            
            Object.keys(filters).forEach(filterName => {
                const filterValue = filters[filterName].toLowerCase();
                const cell = row.querySelector(`td[data-filter="${filterName}"]`);
                
                if (cell) {
                    const cellValue = cell.textContent.toLowerCase();
                    if (!cellValue.includes(filterValue)) {
                        shouldShow = false;
                    }
                }
            });
            
            row.style.display = shouldShow ? '' : 'none';
            if (shouldShow) visibleCount++;
        });
        
        // Atualizar contador se existir
        const counter = table.parentElement.querySelector('.filter-count');
        if (counter) {
            counter.textContent = `${visibleCount} registros encontrados`;
        }
    }

    /**
     * Salva filtros no localStorage
     */
    saveFilters() {
        const filters = this.getActiveFilters();
        localStorage.setItem('reportFilters', JSON.stringify(filters));
    }

    /**
     * Carrega filtros salvos
     */
    loadSavedFilters() {
        const savedFilters = localStorage.getItem('reportFilters');
        if (savedFilters) {
            const filters = JSON.parse(savedFilters);
            
            Object.keys(filters).forEach(filterName => {
                const filterElement = document.querySelector(`[name="${filterName}"]`);
                if (filterElement) {
                    filterElement.value = filters[filterName];
                }
            });
            
            this.applyFilters();
        }
    }

    /**
     * Mostra indicador de carregamento
     */
    showLoading(message = 'Processando...') {
        this.hideLoading();
        
        const loadingDiv = document.createElement('div');
        loadingDiv.id = 'reportLoading';
        loadingDiv.innerHTML = `
            <div class="loading-overlay">
                <div class="loading-spinner">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                    <p class="mt-2">${message}</p>
                </div>
            </div>
        `;
        
        document.body.appendChild(loadingDiv);
        this.loadingElement = loadingDiv;
    }

    /**
     * Oculta indicador de carregamento
     */
    hideLoading() {
        if (this.loadingElement) {
            this.loadingElement.remove();
            this.loadingElement = null;
        }
    }

    /**
     * Mostra notificação
     */
    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'error' ? 'danger' : type} report-notification`;
        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="bi bi-${this.getIconForType(type)} me-2"></i>
                <span>${message}</span>
                <button type="button" class="btn-close ms-auto" aria-label="Fechar"></button>
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Auto-remove após 5 segundos
        setTimeout(() => {
            notification.remove();
        }, 5000);
        
        // Botão de fechar
        notification.querySelector('.btn-close').addEventListener('click', () => {
            notification.remove();
        });
    }

    /**
     * Retorna ícone para tipo de notificação
     */
    getIconForType(type) {
        const icons = {
            success: 'check-circle',
            error: 'exclamation-circle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || icons.info;
    }

    /**
     * Obtém data atual formatada
     */
    getCurrentDate() {
        const now = new Date();
        const day = String(now.getDate()).padStart(2, '0');
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const year = now.getFullYear();
        return `${day}-${month}-${year}`;
    }

    /**
     * Imprime relatório diretamente
     */
    printReport() {
        const element = document.getElementById('relatorioContent');
        if (!element) {
            this.showNotification('Conteúdo do relatório não encontrado', 'error');
            return;
        }

        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>Relatório</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .no-print, .export-button, .btn { display: none !important; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    th { background-color: #f2f2f2; }
                </style>
            </head>
            <body>
                ${element.innerHTML}
            </body>
            </html>
        `);
        
        printWindow.document.close();
        printWindow.print();
    }
}

// Inicializar gerenciador de relatórios
const reportManager = new ReportManager();

// Funções globais para compatibilidade
window.exportToPdf = (elementId, filename, options) => {
    reportManager.generatePDF(document.getElementById(elementId), filename, options);
};

window.exportTableToCSV = (tableId, filename) => {
    reportManager.exportToCSV(tableId);
};

window.exportReport = (reportType, elementId, filename) => {
    reportManager.exportToPDF(reportType);
};

// Adicionar estilos CSS
const reportStyles = `
    .loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
    }

    .loading-spinner {
        background-color: white;
        padding: 2rem;
        border-radius: 0.5rem;
        text-align: center;
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
    }

    .report-notification {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1050;
        min-width: 300px;
        max-width: 500px;
        animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    .filter-count {
        font-size: 0.9rem;
        color: #6c757d;
        margin-top: 0.5rem;
    }
`;

// Adicionar estilos ao documento
const styleSheet = document.createElement('style');
styleSheet.textContent = reportStyles;
document.head.appendChild(styleSheet);
