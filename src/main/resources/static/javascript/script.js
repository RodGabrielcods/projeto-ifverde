document.addEventListener('DOMContentLoaded', function () {

    const calendarioGrid = document.querySelector('.calendar-grid');
    const mesAnoTitulo = document.getElementById('month-year');

    if (calendarioGrid) {
        const ano = 2025;
        const mes = 10;

        const hoje = new Date();
        const diaHoje = hoje.getDate();
        const mesHoje = hoje.getMonth();
        const anoHoje = hoje.getFullYear();

        const primeiroDia = new Date(ano, mes, 1);
        const diaSemanaInicio = primeiroDia.getDay();

        const ultimoDia = new Date(ano, mes + 1, 0);
        const totalDiasMes = ultimoDia.getDate();

        mesAnoTitulo.textContent = primeiroDia.toLocaleString('pt-BR', { month: 'long', year: 'numeric' });

        for (let i = 0; i < diaSemanaInicio; i++) {
            const diaVazio = document.createElement('div');
            diaVazio.classList.add('empty-day');
            calendarioGrid.appendChild(diaVazio);
        }

        for (let dia = 1; dia <= totalDiasMes; dia++) {
            const diaElemento = document.createElement('div');
            diaElemento.classList.add('day');
            diaElemento.textContent = dia;

            if (dia === diaHoje && mes === mesHoje && ano === anoHoje) {
                diaElemento.classList.add('today');
                diaElemento.textContent = `${dia}`;
            }

            calendarioGrid.appendChild(diaElemento);
        }
    }
});