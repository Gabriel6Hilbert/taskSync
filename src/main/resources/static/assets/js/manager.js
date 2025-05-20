 document.addEventListener('DOMContentLoaded', listarTodas);
 const baseUrl = '/api/tarefas';

    function formatarDataHora(isoString) {
    const data = new Date(isoString);
    const dia = String(data.getDate()).padStart(2, '0');
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const ano = data.getFullYear();
    const hora = String(data.getHours()).padStart(2, '0');
    const minuto = String(data.getMinutes()).padStart(2, '0');

    return `${dia}/${mes}/${ano} ${hora}:${minuto}`;
    }

    document.getElementById('filtroStatus').addEventListener('change', aplicarFiltros);
    document.getElementById('filtroPrioridade').addEventListener('change', aplicarFiltros);

    function aplicarFiltros() {
      const statusSelecionado = document.getElementById('filtroStatus').value;
      const prioridadeSelecionada = document.getElementById('filtroPrioridade').value;
      const linhas = document.querySelectorAll('#tarefasContainer tr');

      linhas.forEach(linha => {
        const status = linha.querySelector('td:nth-child(3)').textContent.trim().toUpperCase();
        const prioridade = linha.querySelector('td:nth-child(2)').textContent.trim().toUpperCase();

        const statusOk = !statusSelecionado || status === statusSelecionado;
        const prioridadeOk = !prioridadeSelecionada || prioridade === prioridadeSelecionada;

        linha.style.display = (statusOk && prioridadeOk) ? '' : 'none';
      });
}



    async function listarTodas() {
      try {
        const response = await fetch(`${baseUrl}/listAll`);
        const tarefas = await response.json();
        renderizarTarefas(tarefas);
      } catch (err) {
        console.error('Erro ao listar tarefas', err);
      }
    }

    async function buscarTarefas() {
      const termo = document.getElementById('buscaInput').value.trim().toLowerCase();
      if (!termo) return listarTodas();

      try {
        const todas = await fetch(`${baseUrl}/listAll`);
        const tarefas = await todas.json();
        const filtradas = tarefas.filter(t => 
          t.descricao?.toLowerCase().includes(termo) ||
          t.status?.toLowerCase().includes(termo) ||
          t.prioridade?.toLowerCase().includes(termo)
        );
        renderizarTarefas(filtradas);
      } catch (err) {
        console.error('Erro ao buscar tarefas', err);
      }
    }

    function renderizarTarefas(tarefas) {
    const container = document.getElementById('tarefasContainer');
    container.innerHTML = '';

    if (tarefas.length === 0) {
        container.innerHTML = `<tr><td colspan="6" class="text-muted">Nenhuma tarefa encontrada.</td></tr>`;
        return;
    }

    tarefas.forEach(tarefa => {
        const tr = document.createElement('tr');
        tr.classList.add(`status-${tarefa.status?.toUpperCase() || 'INDEFINIDO'}`);

        const dataFormatada = tarefa.dataHora ? formatarDataHora(tarefa.dataHora) : '---';
        const alertaFormatado = tarefa.alertaEm ? formatarDataHora(tarefa.alertaEm) : '---';

        tr.innerHTML = `
        <td>${tarefa.descricao}</td>
        <td>${tarefa.prioridade}</td>
        <td><strong>${tarefa.status}</strong></td>
        <td>${dataFormatada}</td>
        <td>${alertaFormatado}</td>
        <td>${tarefa.observacoes}</td>
        <td>
            <button class="btn btn-sm btn-outline-primary" onclick="editarTarefa(${tarefa.id})">Editar</button>
            <button class="btn btn-sm btn-outline-danger" onclick="deletarTarefa(${tarefa.id})">Deletar</button>
        </td>
        `;

        container.appendChild(tr);
    });
    }

    async function editarTarefa(id) {
        try {
            const response = await fetch(`${baseUrl}/buscar/${id}`);
            const tarefa = await response.json();

            document.getElementById('editId').value = tarefa.id;
            document.getElementById('editDescricao').value = tarefa.descricao || '';
            document.getElementById('editPrioridade').value = (tarefa.prioridade || '').toUpperCase();
            document.getElementById('editStatus').value = (tarefa.status || '').toUpperCase();
            document.getElementById('editData').value = formatarInputDateTime(tarefa.dataHora);
            document.getElementById('editAlerta').value = formatarInputDateTime(tarefa.alertaEm);
            document.getElementById('editObservacoes').value = tarefa.observacoes || '';

            if (tarefa.linkCalendar) {
              const linkElement = document.getElementById('googleCalendarLink');
              linkElement.href = tarefa.linkCalendar;
              linkElement.textContent = tarefa.linkCalendar;

              document.getElementById('googleCalendarLinkContainer').style.display = 'block';
            } else {
              document.getElementById('googleCalendarLinkContainer').style.display = 'none';
            }

            const modal = new bootstrap.Modal(document.getElementById('modalEditarTarefa'));
            modal.show();
        } catch (err) {
            console.error('Erro ao buscar tarefa para edição', err);
        }
    }

    function formatarInputDateTime(isoString) {
    if (!isoString) return '';
    const data = new Date(isoString);
    const pad = n => String(n).padStart(2, '0');

    return `${data.getFullYear()}-${pad(data.getMonth() + 1)}-${pad(data.getDate())}T${pad(data.getHours())}:${pad(data.getMinutes())}`;
    }


    document.getElementById('formEditarTarefa').addEventListener('submit', async function (e) {
    e.preventDefault();

    const id = document.getElementById('editId').value;
    const tarefaAtualizada = {
        descricao: document.getElementById('editDescricao').value,
        prioridade: document.getElementById('editPrioridade').value,
        status: document.getElementById('editStatus').value,
        dataHora: document.getElementById('editData').value,
        alertaEm: document.getElementById('editAlerta').value,
        observacoes: document.getElementById('editObservacoes').value,
        concluida: false
    };

    try {
        await fetch(`${baseUrl}/atualizar/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(tarefaAtualizada)
        });

        bootstrap.Modal.getInstance(document.getElementById('modalEditarTarefa')).hide();
        listarTodas();
    } catch (err) {
        console.error('Erro ao atualizar tarefa', err);
        alert('Erro ao salvar alterações.');
    }
    });



    async function deletarTarefa(id) {
    const confirmacao = confirm('Tem certeza que deseja excluir esta tarefa?');
    if (!confirmacao) return;

    try {
      const response = await fetch(`${baseUrl}/delete/${id}`, {
        method: 'DELETE'
      });

      if (!response.ok) {
        throw new Error('Falha ao excluir no servidor');
      }

      listarTodas();
    } catch (err) {
      console.error('Erro ao deletar tarefa:', err);
      alert('Erro ao deletar tarefa. Verifique a conexão ou tente novamente.');
    }
  }


   