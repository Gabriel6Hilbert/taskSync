package com.tasksync.v1.service;

import com.google.api.services.calendar.model.Event;
import com.tasksync.v1.integration.GoogleCalendarService;
import com.tasksync.v1.model.mysql.dto.TaskDto;
import com.tasksync.v1.model.mysql.entity.Task;
import com.tasksync.v1.model.mysql.mapper.TaskMapper;
import com.tasksync.v1.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
//release v2.0.0
    private final GoogleCalendarService googleCalendarService;
    private final TaskRepository repository;

    public TaskDto criar(TaskDto dto) {
        Task entity = TaskMapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(null);

        Task salvo = repository.save(entity);

        String linkCalendar = null;
        if (salvo.getAlertaEm() != null) {
            try {
                Event eventoCriado = googleCalendarService.criarEventoTarefa(salvo);

                salvo.setGoogleEventId(eventoCriado.getId());
                salvo.setLink(eventoCriado.getHtmlLink());
                repository.save(salvo);

                linkCalendar = eventoCriado.getHtmlLink();
            } catch (Exception e) {
                System.err.println("Erro ao criar evento no Calendar: " + e.getMessage());
            }
        }

        TaskDto retorno = TaskMapper.toDTO(salvo);
        retorno.setLinkCalendar(linkCalendar);
        return retorno;
    }



    public List<TaskDto> listarTodas() {
        return repository.findAll()
                .stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TaskDto> buscarPorId(Long id) {
        return repository.findById(id)
                .map(TaskMapper::toDTO);
    }

    public TaskDto atualizar(Long id, TaskDto dto) {
        Task existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        existente.setDescricao(dto.getDescricao());
        existente.setDataHora(dto.getDataHora());
        existente.setStatus(dto.getStatus());
        existente.setPrioridade(dto.getPrioridade());
        existente.setUpdatedAt(LocalDateTime.now());
        existente.setResponsavel(dto.getResponsavel());
        existente.setConcluida(dto.getConcluida());
        existente.setObservacoes(dto.getObservacoes());
        existente.setAlertaEm(dto.getAlertaEm());

        Task atualizado = repository.save(existente);

        String linkCalendar = null;

        try {
            if (atualizado.getGoogleEventId() != null) {
                googleCalendarService.atualizarEventoTarefa(atualizado);
                linkCalendar = atualizado.getLink(); // já tem o link salvo no banco
            } else if (atualizado.getAlertaEm() != null) {
                Event eventoCriado = googleCalendarService.criarEventoTarefa(atualizado);
                atualizado.setGoogleEventId(eventoCriado.getId());
                atualizado.setLink(eventoCriado.getHtmlLink());
                repository.save(atualizado);
                linkCalendar = eventoCriado.getHtmlLink();
            }
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar com Google Calendar: " + e.getMessage());
        }

        TaskDto retorno = TaskMapper.toDTO(atualizado);
        retorno.setLinkCalendar(linkCalendar);
        return retorno;
    }




    public void excluir(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        try {
            if (task.getGoogleEventId() != null) {
                googleCalendarService.excluirEventoTarefa(task.getGoogleEventId());
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir evento do Google Calendar: " + e.getMessage());
        }

        repository.deleteById(id);
    }


    public List<TaskDto> listarPorStatus(String status) {
        return repository.findByStatus(status)
                .stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDto> listarPorPrioridade(String prioridade) {
        return repository.findByPrioridade(prioridade)
                .stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }
}
