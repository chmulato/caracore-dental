/**
 * Funcionalidades avançadas para relatórios
 * Extensões para export, filtros e interatividade
 * Versão 1.0.2 - Correção do bug "clone is not defined" e "Unsupported image type" na exportação PDF
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

        const config = {
            filename: `relatorio-${reportType}-${this.getCurrentDate()}.pdf`,
            options: {
                margin: 0.5,
                jsPDF: { 
                    orientation: reportType === 'pacientes' ? 'portrait' : 'landscape', 
                    format: 'a4',
                    compress: true,
                    precision: 16
                },
                html2canvas: { 
                    scale: 2, 
                    useCORS: true,
                    allowTaint: true,
                    logging: false,
                    removeContainer: true,
                    imageTimeout: 0,
                    foreignObjectRendering: false,
                    imageRendering: 'pixelated',
                    ignoreElements: (element) => {
                        // Ignorar elementos que podem causar problemas
                        if (element.tagName === 'IMG' || element.tagName === 'SVG') {
                            return true;
                        }
                        // Ignorar ícones Bootstrap e FontAwesome
                        if (element.tagName === 'I' || element.classList.contains('bi') || 
                            element.classList.contains('icon') || element.classList.contains('fa')) {
                            return true;
                        }
                        return false;
                    }
                }
            }
        };
        
        // Usar diretamente a configuração criada
        this.generatePDF(element, config.filename, config.options);
    }

    /**
     * Gera PDF com tratamento de erros aprimorado
     */
    generatePDF(element, filename, options) {
        try {
            this.showLoading('Gerando PDF...');
            
            // Preparar elemento para exportação
            this.prepareElementForExport(element);
            
            // Usar o elemento temporário para exportação
            const tempElement = document.getElementById('tempPrintElement');
            
            if (!tempElement) {
                throw new Error('Elemento temporário não encontrado');
            }

            // Configurar opções avançadas para lidar com imagens problemáticas
            const enhancedOptions = {
                ...options,
                imageTimeout: 0,
                enableLinks: false,
                html2canvas: {
                    ...options.html2canvas,
                    allowTaint: true,
                    useCORS: true,
                    removeContainer: true,
                    scale: 2,
                    logging: false,
                    imageTimeout: 0,
                    // Usar uma função de ignoreElements mais agressiva
                    ignoreElements: (element) => {
                        // Ignorar elementos que podem causar problemas
                        const tagName = element.tagName?.toLowerCase();
                        if (tagName === 'img' || tagName === 'svg' || tagName === 'canvas') {
                            return true;
                        }
                        // Ignorar elementos com classes de ícones
                        const className = element.className || '';
                        if (typeof className === 'string' && 
                            (className.includes('bi-') || className.includes('icon') || 
                             className.includes('fa-') || className.includes('logo'))) {
                            return true;
                        }
                        return false;
                    }
                }
            };

            html2pdf()
                .from(tempElement)
                .set(enhancedOptions)
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
        } catch (error) {
            console.error('Erro ao iniciar geração de PDF:', error);
            this.hideLoading();
            this.restoreElementAfterExport(element);
            this.showNotification('Erro ao gerar PDF: ' + error.message, 'error');
        }
    }

    /**
     * Prepara elemento para exportação
     */
    prepareElementForExport(element) {
        let clone;
        
        try {
            // Criar um clone do elemento para não modificar o original
            clone = element.cloneNode(true);
            element.parentNode.insertBefore(clone, element);
            element.style.display = 'none';
            clone.id = 'tempPrintElement';
            clone.classList.add('printing');
            
            // Ocultar elementos não necessários
            const hideElements = clone.querySelectorAll('.no-print, .export-button, .btn:not(.badge)');
            hideElements.forEach(el => {
                if (el.parentNode) el.parentNode.removeChild(el);
            });
    
            // REMOVER COMPLETAMENTE TODAS AS IMAGENS para evitar erro "Unsupported image type"
            this._removeAllElementsOfType(clone, 'img');
            
            // REMOVER COMPLETAMENTE SVG para evitar erro "Unsupported image type"
            this._removeAllElementsOfType(clone, 'svg');
            
            // REMOVER COMPLETAMENTE CANVAS para evitar erro "Unsupported image type"
            this._removeAllElementsOfType(clone, 'canvas');
            
            // REMOVER COMPLETAMENTE ÍCONES Bootstrap e outros que podem causar problemas
            const icons = clone.querySelectorAll('i[class*="bi-"], .bi, [class*="icon"], .fa, [class*="fa-"]');
            icons.forEach(icon => {
                if (icon.parentNode) icon.parentNode.removeChild(icon);
            });
    
            // Remover logos e outros elementos visuais problemáticos
            const logos = clone.querySelectorAll('.logo, .brand, [src*="logo"], [src*="icon"], [class*="logo"], [class*="brand"]');
            logos.forEach(logo => {
                if (logo.parentNode) logo.parentNode.removeChild(logo);
            });
    
            // Remover elementos com background-image
            const elementsWithBg = clone.querySelectorAll('*');
            elementsWithBg.forEach(el => {
                if (el.style && el.style.backgroundImage) {
                    el.style.backgroundImage = 'none';
                }
                // Remover também através de getComputedStyle
                const style = window.getComputedStyle(el);
                if (style.backgroundImage && style.backgroundImage !== 'none') {
                    el.style.backgroundImage = 'none';
                }
            });
    
            // Remover elementos com data-url ou src que podem conter imagens
            const elementsWithSrc = clone.querySelectorAll('[src], [data-src], [data-background], [style*="url"]');
            elementsWithSrc.forEach(el => {
                if (el.tagName !== 'SCRIPT' && el.tagName !== 'LINK') {
                    if (el.parentNode) {
                        el.parentNode.removeChild(el);
                    }
                }
            });
            
            // Ajustar tabelas para impressão
            const tables = clone.querySelectorAll('.table-responsive');
            tables.forEach(table => {
                table.style.overflow = 'visible';
            });
        } catch (error) {
            console.error('Erro ao preparar elemento para exportação:', error);
            return; // Abortar se ocorrer um erro na preparação
        }

        // Adicionar CSS específico para impressão
        const printStyles = document.createElement('style');
        printStyles.id = 'temp-print-styles';
        printStyles.textContent = `
            .printing * {
                -webkit-print-color-adjust: exact !important;
                color-adjust: exact !important;
            }
            .printing img,
            .printing svg,
            .printing i[class*="bi-"],
            .printing .bi,
            .printing [class*="icon"],
            .printing .fa,
            .printing [class*="fa-"] {
                display: none !important;
            }
            .printing .table {
                font-size: 12px !important;
            }
            .printing .table th,
            .printing .table td {
                padding: 4px !important;
                border: 1px solid #000 !important;
            }
        `;
        document.head.appendChild(printStyles);
    }

    /**
     * Restaura elemento após exportação
     */
    restoreElementAfterExport(element) {
        // Remover o clone temporário
        const tempElement = document.getElementById('tempPrintElement');
        if (tempElement) {
            tempElement.parentNode.removeChild(tempElement);
        }
        
        // Exibir o elemento original novamente
        element.style.display = '';
        
        // Remover estilos temporários
        const tempStyles = document.getElementById('temp-print-styles');
        if (tempStyles) {
            tempStyles.remove();
        }

        // Restaurar elementos com src
        const elementsWithSrc = element.querySelectorAll('[src], [data-src], [data-background]');
        elementsWithSrc.forEach(el => {
            if (el.tagName !== 'SCRIPT' && el.tagName !== 'LINK' && el.dataset.originalDisplay !== undefined) {
                el.style.display = el.dataset.originalDisplay;
                delete el.dataset.originalDisplay;
            }
        });

        // Restaurar tabelas
        const tables = element.querySelectorAll('.table-responsive');
        tables.forEach(table => {
            table.style.overflow = '';
        });

        // Restaurar backgrounds
        const elementsWithBg = element.querySelectorAll('[style*="background-image"]');
        elementsWithBg.forEach(el => {
            if (el.dataset.originalBackground) {
                el.style.backgroundImage = el.dataset.originalBackground;
                delete el.dataset.originalBackground;
            }
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

    /**
     * Método auxiliar para remover todos os elementos de um tipo específico
     */
    _removeAllElementsOfType(parentElement, tagName) {
        try {
            const elements = parentElement.querySelectorAll(tagName);
            elements.forEach(el => {
                if (el.parentNode) el.parentNode.removeChild(el);
            });
            
            // Segunda verificação para garantir que todos foram removidos
            const remainingElements = parentElement.getElementsByTagName(tagName);
            while (remainingElements.length > 0) {
                if (remainingElements[0].parentNode) {
                    remainingElements[0].parentNode.removeChild(remainingElements[0]);
                }
            }
        } catch (error) {
            console.error(`Erro ao remover elementos ${tagName}:`, error);
        }
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

// Adicionar estilos ao documento apenas se não existir
if (!document.getElementById('report-manager-styles')) {
    const styleSheet = document.createElement('style');
    styleSheet.id = 'report-manager-styles';
    styleSheet.textContent = reportStyles;
    document.head.appendChild(styleSheet);
}
