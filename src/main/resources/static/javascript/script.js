document.addEventListener('DOMContentLoaded', function () {
    const hamburger = document.querySelector('.hamburger');
    const navMenu = document.querySelector('nav ul');

    if (hamburger) {
        hamburger.addEventListener('click', function () {
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
        });

        document.addEventListener('click', function (event) {
            if (!event.target.closest('header')) {
                hamburger.classList.remove('active');
                navMenu.classList.remove('active');
            }
        });

        document.querySelectorAll('nav a').forEach(link => {
            link.addEventListener('click', function () {
                hamburger.classList.remove('active');
                navMenu.classList.remove('active');
            });
        });
    }

    const calendarioGrid = document.querySelector('.calendar-grid');
    const mesAnoTitulo = document.getElementById('month-year');
    const eventModal = document.getElementById('event-modal');
    const closeModal = document.querySelector('.modal-close');
    const saveEventBtn = document.getElementById('save-event');
    
    let selectedDate = null;
    let eventos = JSON.parse(localStorage.getItem('ifverde-eventos')) || {};
    
    let dataAtualCalendario = new Date();

    if (calendarioGrid) {
        const prevBtn = document.getElementById('prev-month');
        const nextBtn = document.getElementById('next-month');

        function atualizarExibicaoCalendario() {
            const ano = dataAtualCalendario.getFullYear();
            const mes = dataAtualCalendario.getMonth();
            renderizarCalendario(ano, mes);
            mesAnoTitulo.textContent = new Date(ano, mes, 1).toLocaleString('pt-BR', { month: 'long', year: 'numeric' });
        }

        if (prevBtn) {
            prevBtn.addEventListener('click', function () {
                dataAtualCalendario.setMonth(dataAtualCalendario.getMonth() - 1);
                atualizarExibicaoCalendario();
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', function () {
                dataAtualCalendario.setMonth(dataAtualCalendario.getMonth() + 1);
                atualizarExibicaoCalendario();
            });
        }

        atualizarExibicaoCalendario();
    }

    function renderizarCalendario(ano, mes) {
        const calendarioGrid = document.querySelector('.calendar-grid');
        const diasExistentes = calendarioGrid.querySelectorAll('.day, .empty-day, .today');
        diasExistentes.forEach(dia => dia.remove());

        const hoje = new Date();
        const diaHoje = hoje.getDate();
        const mesHoje = hoje.getMonth();
        const anoHoje = hoje.getFullYear();

        const primeiroDia = new Date(ano, mes, 1);
        const diaSemanaInicio = primeiroDia.getDay();

        const ultimoDia = new Date(ano, mes + 1, 0);
        const totalDiasMes = ultimoDia.getDate();

        for (let i = 0; i < diaSemanaInicio; i++) {
            const diaVazio = document.createElement('div');
            diaVazio.classList.add('empty-day');
            calendarioGrid.appendChild(diaVazio);
        }

        for (let dia = 1; dia <= totalDiasMes; dia++) {
            const diaElemento = document.createElement('div');
            diaElemento.classList.add('day');
            diaElemento.style.cursor = 'pointer';

            const dataFormatada = `${dia.toString().padStart(2, '0')}/${(mes + 1).toString().padStart(2, '0')}/${ano}`;
            const eventosDia = eventos[dataFormatada] || [];

            diaElemento.innerHTML = `<span class="day-number">${dia}</span>`;

            if (eventosDia.length > 0) {
                const eventosHtml = eventosDia.map(evento => {
                    let classe = 'event-outro';
                    if (evento.tipo === 'colheita') classe = 'event-colheita';
                    else if (evento.tipo === 'plantacao') classe = 'event-plantacao';
                    else if (evento.tipo === 'pagamento') classe = 'event-pagamento';
                    return `<span class="event-marker ${classe}" title="${evento.tipo}" style="display:inline-block; width:8px; height:8px; border-radius:50%; background-color:#4ade80; margin: 2px;"></span>`;
                }).join('');
                diaElemento.innerHTML += `<div class="day-events" style="margin-top: 5px;">${eventosHtml}</div>`;
            }

            if (dia === diaHoje && mes === mesHoje && ano === anoHoje) {
                diaElemento.classList.add('today');
            }

            diaElemento.addEventListener('click', function () {
                selectedDate = dataFormatada;
                abrirModalEvento(dataFormatada);
            });

            calendarioGrid.appendChild(diaElemento);
        }
    }

    function abrirModalEvento(data) {
        if (!eventModal) return;

        const eventosExistentes = eventos[data] || [];
        const listaEventos = document.getElementById('events-list');

        if (listaEventos) {
            listaEventos.innerHTML = '';
            eventosExistentes.forEach((evento, index) => {
                const eventoItem = document.createElement('div');
                eventoItem.style.cssText = 'display: flex; justify-content: space-between; align-items: center; padding: 0.8rem; background: #0f172a; border-radius: 6px; margin-bottom: 0.5rem; border-left: 4px solid #4ade80;';
                eventoItem.innerHTML = `
                    <span style="font-size: 0.9rem;"><strong>${evento.tipo.toUpperCase()}</strong> - ${evento.descricao}</span>
                    <button onclick="removerEvento('${data}', ${index})" class="btn btn-danger btn-sm" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;">Excluir</button>
                `;
                listaEventos.appendChild(eventoItem);
            });
            if(eventosExistentes.length === 0){
                listaEventos.innerHTML = '<p style="color: #94a3b8; font-size: 0.9rem; text-align: center;">Nenhum evento registrado para este dia.</p>';
            }
        }

        const titleModal = document.getElementById('modal-date-title');
        if (titleModal) {
            titleModal.textContent = `Eventos: ${data}`;
        }

        eventModal.style.display = 'block';
    }

    if (closeModal) {
        closeModal.addEventListener('click', function () {
            eventModal.style.display = 'none';
        });
    }

    window.addEventListener('click', function (event) {
        if (event.target === eventModal) {
            eventModal.style.display = 'none';
        }
    });

    if (saveEventBtn) {
        saveEventBtn.addEventListener('click', function () {
            const tipoEvento = document.getElementById('event-type').value;
            const descricaoEvento = document.getElementById('event-description').value;

            if (!tipoEvento || !descricaoEvento) {
                alert('Preencha o tipo e a descrição do evento.');
                return;
            }

            if (!eventos[selectedDate]) {
                eventos[selectedDate] = [];
            }

            eventos[selectedDate].push({
                tipo: tipoEvento,
                descricao: descricaoEvento
            });

            localStorage.setItem('ifverde-eventos', JSON.stringify(eventos));

            document.getElementById('event-type').value = '';
            document.getElementById('event-description').value = '';

            abrirModalEvento(selectedDate);
            renderizarCalendario(dataAtualCalendario.getFullYear(), dataAtualCalendario.getMonth());
        });
    }

    window.removerEvento = function (data, index) {
        if (eventos[data]) {
            eventos[data].splice(index, 1);
            if (eventos[data].length === 0) {
                delete eventos[data];
            }
            localStorage.setItem('ifverde-eventos', JSON.stringify(eventos));
            abrirModalEvento(data);
            renderizarCalendario(dataAtualCalendario.getFullYear(), dataAtualCalendario.getMonth());
        }
    };

    const tabelaResponsiva = document.querySelector('.table-responsive');
    if (tabelaResponsiva) {
        const tabelas = tabelaResponsiva.querySelectorAll('table');
        tabelas.forEach(tabela => {
            if (window.innerWidth <= 768) {
                let colunas = tabela.querySelectorAll('th');
                tabela.querySelectorAll('tbody tr').forEach(linha => {
                    let celulas = linha.querySelectorAll('td');
                    celulas.forEach((celula, index) => {
                        if (colunas[index]) {
                            celula.setAttribute('data-label', colunas[index].textContent);
                        }
                    });
                });
            }
        });
    }
});