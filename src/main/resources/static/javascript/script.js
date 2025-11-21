// Espera todo o conteúdo da página carregar antes de rodar o script
document.addEventListener('DOMContentLoaded', function() {

    // --- 1. LÓGICA DO CALENDÁRIO ---
    
    // Tenta encontrar o elemento do calendário na página
    const calendarioGrid = document.querySelector('.calendar-grid');
    const mesAnoTitulo = document.getElementById('month-year');
    
    // Só executa o código do calendário se estiver na página certa
    if (calendarioGrid) {
        // Ano e Mês (Novembro 2025)
        // Em JavaScript, os meses são de 0 (Jan) a 11 (Dez), então Novembro é 10.
        const ano = 2025;
        const mes = 10; // Novembro

        const hoje = new Date();
        const diaHoje = hoje.getDate();
        const mesHoje = hoje.getMonth();
        const anoHoje = hoje.getFullYear();

        // Data do primeiro dia do mês especificado
        const primeiroDia = new Date(ano, mes, 1);
        // Pega o dia da semana do primeiro dia (0 = Dom, 1 = Seg, ...)
        const diaSemanaInicio = primeiroDia.getDay();

        // Data do último dia do mês (usamos um truque: dia 0 do mês seguinte)
        const ultimoDia = new Date(ano, mes + 1, 0);
        const totalDiasMes = ultimoDia.getDate();
        
        // Atualiza o título (Ex: "Novembro 2025")
        // Estamos usando o 'toLocaleString' para pegar o nome do mês em português
        mesAnoTitulo.textContent = primeiroDia.toLocaleString('pt-BR', { month: 'long', year: 'numeric' });

        // 1. Preenche os dias vazios antes do dia 1
        for (let i = 0; i < diaSemanaInicio; i++) {
            const diaVazio = document.createElement('div');
            diaVazio.classList.add('empty-day');
            calendarioGrid.appendChild(diaVazio);
        }

        // 2. Preenche os dias do mês (1 a 31)
        for (let dia = 1; dia <= totalDiasMes; dia++) {
            const diaElemento = document.createElement('div');
            diaElemento.classList.add('day');
            diaElemento.textContent = dia;

            // Marca o dia de hoje, se for o mês e ano atuais
            if (dia === diaHoje && mes === mesHoje && ano === anoHoje) {
                diaElemento.classList.add('today');
                diaElemento.textContent = `${dia}`; // Adiciona "Hoje"
            }

            calendarioGrid.appendChild(diaElemento);
        }
    }
});