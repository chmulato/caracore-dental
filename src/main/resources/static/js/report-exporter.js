/**
 * Funcionalidades para exportação de relatórios
 * Compatibilidade com report-manager.js
 * Versão 1.0.8 - Detecção inteligente de gráficos na página para evitar carregamento desnecessário do Chart.js
 */

// Função global para carregar script dinamicamente
function loadScript(url, callback) {
    console.log('Carregando script:', url);
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.onload = function() {
        console.log('Script carregado com sucesso:', url);
        if (callback) callback();
    };
    script.onerror = function() {
        console.error('Erro ao carregar script:', url);
    };
    document.head.appendChild(script);
}

// Verificar se jQuery está disponível e carregá-lo se não estiver
if (typeof $ === 'undefined') {
    console.warn('jQuery não foi encontrado. Carregando jQuery dinamicamente...');
    
    // Carregar jQuery e depois Chart.js se necessário
    loadScript('/webjars/jquery/3.7.0/jquery.min.js', function() {
        console.log('jQuery carregado com sucesso');
        
        // Verificar se há gráficos na página
        const hasCharts = document.querySelector('canvas[id*="Chart"]') !== null;
        
        if (!hasCharts) {
            console.log('Não há gráficos nesta página. Ignorando carregamento de Chart.js');
            return;
        }
        
        // Verificar se precisamos do Chart.js para os gráficos
        if (typeof Chart === 'undefined') {
            console.log('Gráficos encontrados na página, carregando Chart.js');
            loadScript('/webjars/chart.js/4.3.0/dist/chart.umd.js', function() {
                console.log('Chart.js carregado com sucesso');
                initializeCharts();
            });
        } else {
            // Inicializar gráficos se Chart.js já estiver disponível
            console.log('Chart.js já está disponível, inicializando gráficos');
            initializeCharts();
        }
    });
} else {
    console.log('jQuery já está disponível');
    
    // Verificar se há qualquer gráfico na página
    const hasCharts = document.querySelector('canvas[id*="Chart"]') !== null;
    
    // Inicializar gráficos se Chart.js estiver disponível e houver gráficos na página
    if (hasCharts && typeof Chart !== 'undefined') {
        console.log('Chart.js disponível e gráficos encontrados na página');
        initializeCharts();
    } else if (hasCharts) {
        // Carregar Chart.js se necessário e se houver gráficos
        console.log('Gráficos encontrados na página, carregando Chart.js');
        loadScript('/webjars/chart.js/4.3.0/dist/chart.umd.js', function() {
            console.log('Chart.js carregado com sucesso');
            initializeCharts();
        });
    } else {
        console.log('Não há gráficos nesta página. Ignorando carregamento de Chart.js');
    }
}

// Funções de exportação - Compatibilidade com report-manager.js
function exportToCSV(tableId, filename) {
    if (typeof reportManager !== 'undefined') {
        reportManager.exportToCSV(tableId);
    } else {
        console.error('reportManager não está definido. Certifique-se de que report-manager.js foi carregado.');
    }
}

function exportToPDF(reportType) {
    if (typeof reportManager !== 'undefined') {
        reportManager.exportToPDF(reportType);
    } else {
        console.error('reportManager não está definido. Certifique-se de que report-manager.js foi carregado.');
    }
}

// Inicializar gráficos de desempenho e agendamentos se estiverem presentes na página
function initializeCharts() {
    // Inicializar gráfico de status de agendamentos, se estiver presente
    if (document.getElementById('statusChartAgendamentos')) {
        try {
            console.log('Inicializando gráfico de status de agendamentos');
            
            // Verificar se a função está disponível no escopo global
            if (typeof window.initializeStatusChart === 'function') {
                window.initializeStatusChart();
            } else if (typeof initializeStatusChart === 'function') {
                initializeStatusChart();
            } else {
                console.warn('Função initializeStatusChart não está definida. Esperando script carregar...');
                // Esperar um pouco e tentar novamente (talvez o script esteja carregando)
                setTimeout(function() {
                    if (typeof window.initializeStatusChart === 'function') {
                        window.initializeStatusChart();
                    } else if (typeof initializeStatusChart === 'function') {
                        initializeStatusChart();
                    } else {
                        console.error('Função initializeStatusChart não encontrada após espera.');
                    }
                }, 1000);
            }
        } catch (e) {
            console.error('Erro ao inicializar gráfico de status:', e);
        }
    }
    
    // Inicializar gráficos de desempenho, se presentes
    if (!document.getElementById('performanceChart') || typeof dentistaNomes === 'undefined') {
        return; // Se não temos o gráfico de desempenho, paramos aqui
    }
    
    console.log('Inicializando gráficos de desempenho');
    
    const ctx = document.getElementById('performanceChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: dentistaNomes,
            datasets: [{
                label: 'Taxa de Realização (%)',
                data: taxasRealizacao,
                backgroundColor: taxasRealizacao.map(taxa => 
                    taxa >= 80 ? 'rgba(40, 167, 69, 0.8)' :  // Verde para >=80%
                    taxa >= 60 ? 'rgba(255, 193, 7, 0.8)' :  // Amarelo para >=60%
                    'rgba(220, 53, 69, 0.8)'                 // Vermelho para <60%
                ),
                borderColor: taxasRealizacao.map(taxa => 
                    taxa >= 80 ? 'rgb(40, 167, 69)' :
                    taxa >= 60 ? 'rgb(255, 193, 7)' :
                    'rgb(220, 53, 69)'
                ),
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.dataset.label + ': ' + context.raw.toFixed(1) + '%';
                        }
                    }
                },
                legend: {
                    display: false
                }
            }
        }
    });
    
    // Gráfico de comparação entre agendados e realizados
    if (document.getElementById('comparacaoChart')) {
        const ctxComparacao = document.getElementById('comparacaoChart').getContext('2d');
        new Chart(ctxComparacao, {
            type: 'bar',
            data: {
                labels: dentistaNomes,
                datasets: [
                    {
                        label: 'Total de Agendamentos',
                        data: totaisAgendamentos,
                        backgroundColor: 'rgba(108, 117, 125, 0.8)',
                        borderColor: 'rgb(108, 117, 125)',
                        borderWidth: 1
                    },
                    {
                        label: 'Consultas Realizadas',
                        data: realizados,
                        backgroundColor: 'rgba(0, 123, 255, 0.8)',
                        borderColor: 'rgb(0, 123, 255)',
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
}

// Inicializar DataTables e botões de exportação se jQuery estiver disponível
$(document).ready(function() {
    // Inicializar DataTables
    if ($.fn.DataTable) {
        $('.data-table').DataTable({
            "language": {
                "url": "/js/dataTables.portuguese-brasil.json"
            },
            "pageLength": 10,
            "responsive": true
        });
    }
    
    // Verificar se há gráficos na página que precisam ser inicializados
    const hasCharts = document.querySelector('canvas[id*="Chart"]') !== null;
    
    // Se não há gráficos na página, não precisa carregar ou verificar Chart.js
    if (!hasCharts) {
        console.log('Não foram encontrados gráficos nesta página. Ignorando inicialização de Chart.js.');
    } 
    // Se há gráficos, então verificamos e inicializamos Chart.js conforme necessário
    else if (typeof Chart !== 'undefined') {
        console.log('Chart.js está disponível no document ready');
        // Inicializar gráficos se a função estiver disponível e não tiverem sido inicializados ainda
        if (typeof window.initializeCharts === 'function' && window.chartsInitialized !== true) {
            console.log('Chamando initializeCharts da função document ready');
            // Adiar a chamada para garantir que todos os scripts foram carregados
            setTimeout(function() {
                window.initializeCharts();
            }, 100);
        } else {
            console.log('Gráficos já foram inicializados ou função não está disponível');
        }
    } else {
        console.warn('Chart.js não está disponível para inicialização dos gráficos');
    }
    
    // Configurar botões de exportação
    $('#btnExportPDF').click(function() {
        const pageUrl = window.location.pathname;
        let reportType = 'relatorio';
        
        // Detectar tipo de relatório com base na URL
        if (pageUrl.includes('agendamentos')) {
            reportType = 'agendamentos';
        } else if (pageUrl.includes('pacientes')) {
            reportType = 'pacientes';
        } else if (pageUrl.includes('desempenho')) {
            reportType = 'desempenho';
        }
        
        console.log(`Exportando relatório do tipo: ${reportType}`);
        reportManager.exportToPDF(reportType);
    });
    
    // Configurar botão de exportação CSV
    $('#btnExportCSV').click(function() {
        let tableId = $(this).data('table-id') || 'dataTable';
        
        if (window.location.pathname.includes('pacientes')) {
            tableId = 'tabelaPacientes';
        } else if (window.location.pathname.includes('agendamentos')) {
            tableId = 'tabelaAgendamentos';
        }
        
        console.log(`Exportando tabela: ${tableId}`);
        reportManager.exportToCSV(tableId);
    });
});
