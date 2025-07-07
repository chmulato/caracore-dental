/**
 * Funcionalidades para exportação de relatórios
 * Compatibilidade com report-manager.js
 * Versão 1.0.0
 */

// Verificar se jQuery está disponível e carregá-lo se não estiver
if (typeof $ === 'undefined') {
    console.warn('jQuery não foi encontrado. Carregando jQuery dinamicamente...');
    
    // Função para carregar script dinamicamente
    function loadScript(url, callback) {
        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = url;
        script.onload = callback;
        document.head.appendChild(script);
    }
    
    // Carregar jQuery e depois Chart.js se necessário
    loadScript('/webjars/jquery/3.7.0/jquery.min.js', function() {
        console.log('jQuery carregado com sucesso');
        
        // Verificar se precisamos do Chart.js
        if (document.getElementById('performanceChart') && typeof Chart === 'undefined') {
            loadScript('/webjars/chart.js/3.7.1/chart.min.js', function() {
                console.log('Chart.js carregado com sucesso');
                initializeCharts();
            });
        } else {
            // Inicializar gráficos se Chart.js já estiver disponível
            if (document.getElementById('performanceChart') && typeof Chart !== 'undefined') {
                initializeCharts();
            }
        }
    });
} else {
    console.log('jQuery já está disponível');
    
    // Inicializar gráficos se Chart.js estiver disponível
    if (document.getElementById('performanceChart') && typeof Chart !== 'undefined') {
        initializeCharts();
    } else if (document.getElementById('performanceChart')) {
        // Carregar Chart.js se necessário
        loadScript('/webjars/chart.js/3.7.1/chart.min.js', function() {
            console.log('Chart.js carregado com sucesso');
            initializeCharts();
        });
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

// Inicializar gráficos de desempenho se estiverem presentes na página
function initializeCharts() {
    if (!document.getElementById('performanceChart') || typeof dentistaNomes === 'undefined') {
        return;
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

// Inicializar DataTables se jQuery e DataTable estiverem disponíveis
$(document).ready(function() {
    if ($.fn.DataTable) {
        $('.data-table').DataTable({
            "language": {
                "url": "/js/dataTables.portuguese-brasil.json"
            },
            "pageLength": 10,
            "responsive": true
        });
    }
});
