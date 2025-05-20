document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("taskForm");
  const mensagem = document.getElementById("mensagem");
  const baseUrl = '/api/tarefas';


  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = {
      descricao: document.getElementById("descricao").value,
      responsavel: document.getElementById("responsavel").value,
      dataHora: document.getElementById("dataHora").value,
      alertaEm: document.getElementById("alertaEm").value,
      prioridade: document.getElementById("prioridade").value,
      status: document.getElementById("status").value,
      observacoes: document.getElementById("observacoes").value,
      concluida: false
    };

    try {
      const response = await fetch(`${baseUrl}/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (response.ok) {
        mensagem.className = "alert alert-success d-flex justify-content-between align-items-center";
        mensagem.innerHTML = `
          <span>✅ Tarefa criada com sucesso!</span>
          ${data.linkCalendar ? `<a href="${data.linkCalendar}" target="_blank" class="btn btn-sm btn-success ms-3">Ver no Google Calendar</a>` : ""}
        `;
        mensagem.style.display = "block";
        form.reset();
      } else {
        mensagem.textContent = "❌ Erro ao criar tarefa.";
        mensagem.className = "alert alert-danger";
        mensagem.style.display = "block";
      }
    } catch (error) {
      mensagem.textContent = "❌ Erro na requisição: " + error.message;
      mensagem.className = "alert alert-danger";
      mensagem.style.display = "block";
    }
  });
});
