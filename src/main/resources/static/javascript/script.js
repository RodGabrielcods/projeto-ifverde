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

    if (calendarioGrid) {
        const agora = new Date();
        const ano = agora.getFullYear();
        const mes = agora.getMonth();

        renderizarCalendario(ano, mes);
        mesAnoTitulo.textContent = new Date(ano, mes, 1).toLocaleString('pt-BR', { month: 'long', year: 'numeric' });

        const prevBtn = document.getElementById('prev-month');
        const nextBtn = document.getElementById('next-month');

        if (prevBtn) {
            prevBtn.addEventListener('click', function () {
                const mesAnterior = mes - 1 < 0 ? 11 : mes - 1;
                const anoAnterior = mes - 1 < 0 ? ano - 1 : ano;
                renderizarCalendario(anoAnterior, mesAnterior);
                mesAnoTitulo.textContent = new Date(anoAnterior, mesAnterior, 1).toLocaleString('pt-BR', { month: 'long', year: 'numeric' });
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', function () {
                const mesPosterior = mes + 1 > 11 ? 0 : mes + 1;
                const anoPosterior = mes + 1 > 11 ? ano + 1 : ano;
                renderizarCalendario(anoPosterior, mesPosterior);
                mesAnoTitulo.textContent = new Date(anoPosterior, mesPosterior, 1).toLocaleString('pt-BR', { month: 'long', year: 'numeric' });
            });
        }
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

            const dataFormatada = `${dia.toString().padStart(2, '0')}/${(mes + 1).toString().padStart(2, '0')}/${ano}`;
            const eventosDia = eventos[dataFormatada] || [];

            diaElemento.innerHTML = `<span class="day-number">${dia}</span>`;

            if (eventosDia.length > 0) {
                const eventosHtml = eventosDia.map(evento => {
                    let classe = 'event-outro';
                    if (evento.tipo === 'colheita') classe = 'event-colheita';
                    else if (evento.tipo === 'plantacao') classe = 'event-plantacao';
                    else if (evento.tipo === 'pagamento') classe = 'event-pagamento';
                    return `<span class="event-marker ${classe}" title="${evento.tipo}"></span>`;
                }).join('');
                diaElemento.innerHTML += `<div class="day-events">${eventosHtml}</div>`;
            }

            if (eventosDia.length > 0) {
                const primeiroEvento = eventosDia[0];
                if (primeiroEvento.tipo === 'colheita') {
                    diaElemento.classList.add('day-marked-colheita');
                } else if (primeiroEvento.tipo === 'plantacao') {
                    diaElemento.classList.add('day-marked-plantacao');
                } else if (primeiroEvento.tipo === 'pagamento') {
                    diaElemento.classList.add('day-marked-pagamento');
                }
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
                eventoItem.style.cssText = 'display: flex; justify-content: space-between; align-items: center; padding: 0.5rem; background: #0f172a; border-radius: 6px; margin-bottom: 0.5rem;';
                eventoItem.innerHTML = `
                    <span>${evento.tipo} - ${evento.descricao}</span>
                    <button onclick="removerEvento('${data}', ${index})" class="btn btn-danger btn-sm">Remover</button>
                `;
                listaEventos.appendChild(eventoItem);
            });
        }

        const titleModal = document.getElementById('modal-date-title');
        if (titleModal) {
            titleModal.textContent = `Eventos do dia ${data}`;
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
                alert('Preencha o tipo e a descrição do evento');
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
            renderizarCalendario(new Date().getFullYear(), new Date().getMonth());
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
            renderizarCalendario(new Date().getFullYear(), new Date().getMonth());
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
