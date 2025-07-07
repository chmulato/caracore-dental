/**
 * Funcionalidades para exportação de relatórios
 * Compatibilidade com report-manager.js
 * Versão 1.0.9 - Correção de carregamento do Chart.js com MIME type correto e prevenção de inicialização duplicada de gráficos
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
    script.onerror = function(e) {
        console.error('Erro ao carregar script:', url, e);
        // Tentar fallback para Chart.js se for o caso
        if (url.includes('chart.js') || url.includes('Chart.js')) {
            var fallbackUrl = '/js/lib/chart.umd.js';
            console.log('Tentando fallback para:', fallbackUrl);
            loadJsWithCorrectMIME(fallbackUrl, callback);
        }
    };
    document.head.appendChild(script);
}

// Função para carregar script com tipo MIME correto
function loadJsWithCorrectMIME(url, callback) {
    console.log('Carregando script com tipo MIME correto:', url);
    
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                // Criar script element e executá-lo
                var script = document.createElement('script');
                script.type = 'text/javascript';
                script.text = xhr.responseText;
                document.head.appendChild(script);
                console.log('Script carregado e executado com sucesso');
                if (callback) callback();
            } else {
                console.error('Falha ao carregar script. Status:', xhr.status);
            }
        }
    };
    xhr.send();
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
            // Usar caminho local para evitar problemas de MIME type e 404
            loadJsWithCorrectMIME('/js/lib/chart.umd.js', function() {
                console.log('Chart.js carregado com sucesso');
                // Verificar se os gráficos já foram inicializados
                if (!window.chartsInitialized) {
                    initializeCharts();
                }
            });
        } else {
            // Inicializar gráficos se Chart.js já estiver disponível e não inicializado ainda
            console.log('Chart.js já está disponível, verificando inicialização');
            if (!window.chartsInitialized) {
                initializeCharts();
            } else {
                console.log('Gráficos já inicializados, ignorando.');
            }
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
    // Verificar se os gráficos já foram inicializados
    if (window.chartsInitialized === true) {
        console.log('Gráficos já inicializados anteriormente. Ignorando inicialização duplicada.');
        return;
    }
    
    // Marcar que os gráficos estão sendo inicializados
    window.chartsInitialized = true;
    
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
    // Se já inicializamos os gráficos, não fazer nada
    else if (window.chartsInitialized === true) {
        console.log('Gráficos já foram inicializados anteriormente. Nada a fazer.');
    }
    // Se há gráficos e Chart.js já está disponível
    else if (typeof Chart !== 'undefined') {
        console.log('Chart.js está disponível no document ready');
        // Inicializar gráficos se a função estiver disponível
        if (typeof window.initializeCharts === 'function') {
            console.log('Chamando initializeCharts da função document ready');
            // Adiar a chamada para garantir que todos os scripts foram carregados
            setTimeout(function() {
                window.initializeCharts();
            }, 100);
        } else {
            console.log('Função initializeCharts não está disponível');
        }
    } else {
        console.log('Chart.js não está disponível, carregando a biblioteca...');
        
        // Carregar Chart.js do caminho local para evitar problemas de MIME
        loadJsWithCorrectMIME('/js/lib/chart.umd.js', function() {
            console.log('Chart.js carregado com sucesso no document ready');
            
            // Esperar um pouco para garantir que o objeto Chart está disponível
            setTimeout(function() {
                if (typeof window.initializeCharts === 'function' && !window.chartsInitialized) {
                    console.log('Inicializando gráficos após carregamento bem-sucedido de Chart.js');
                    window.initializeCharts();
                }
            }, 200);
        });
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
