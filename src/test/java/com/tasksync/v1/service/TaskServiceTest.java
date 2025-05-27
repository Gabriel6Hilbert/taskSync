package com.tasksync.v1.service;

import com.google.api.services.calendar.model.Event;
import com.tasksync.v1.integration.GoogleCalendarService;
import com.tasksync.v1.model.mysql.dto.TaskDto;
import com.tasksync.v1.model.mysql.entity.Task;
import com.tasksync.v1.model.mysql.mapper.TaskMapper;
import com.tasksync.v1.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private TaskRepository taskRepository;

    private TaskDto dto;
    private Task entity;

    @BeforeEach
    void setUp() {
        dto = TaskDto.builder()
                .descricao("Tarefa de Teste")
                .dataHora(LocalDateTime.now())
                .status("PENDENTE")
                .prioridade("ALTA")
                .responsavel("Usuário")
                .concluida(false)
                .observacoes("Teste")
                .alertaEm(LocalDateTime.now().plusDays(1))
                .build();

        entity = TaskMapper.toEntity(dto);
    }

    @Test
    void deveCriarTarefaComEventoCalendar() throws Exception {
        Task salvo = TaskMapper.toEntity(dto);
        salvo.setId(1L);

        Event event = new Event();
        event.setId("abc123");
        event.setHtmlLink("http://calendar");

        when(taskRepository.save(any(Task.class))).thenReturn(salvo);
        when(googleCalendarService.criarEventoTarefa(any(Task.class))).thenReturn(event);

        TaskDto retorno = taskService.criar(dto);

        assertNotNull(retorno);
        assertEquals("http://calendar", retorno.getLinkCalendar());
        verify(taskRepository, times(2)).save(any(Task.class));
        verify(googleCalendarService).criarEventoTarefa(any(Task.class));
    }

    @Test
    void deveAtualizarTarefaComEventoExistente() throws Exception {
        Task existente = TaskMapper.toEntity(dto);
        existente.setId(1L);
        existente.setGoogleEventId("evt123");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(taskRepository.save(any(Task.class))).thenReturn(existente);

        TaskDto atualizado = taskService.atualizar(1L, dto);

        assertNotNull(atualizado);
        verify(googleCalendarService).atualizarEventoTarefa(any(Task.class));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deveAtualizarTarefaCriandoNovoEventoSeNaoTiverId() throws Exception {
        Task existente = TaskMapper.toEntity(dto);
        existente.setId(1L);
        existente.setGoogleEventId(null);

        Event event = new Event();
        event.setId("novoEvento");
        event.setHtmlLink("http://novolink");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(taskRepository.save(any(Task.class))).thenReturn(existente);
        when(googleCalendarService.criarEventoTarefa(any(Task.class))).thenReturn(event);

        TaskDto atualizado = taskService.atualizar(1L, dto);

        assertNotNull(atualizado);
        assertEquals("http://novolink", atualizado.getLinkCalendar());
        verify(taskRepository, times(2)).save(any(Task.class));
        verify(googleCalendarService).criarEventoTarefa(any(Task.class));
    }

    @Test
    void deveExcluirTarefaComEvento() throws Exception {
        Task existente = TaskMapper.toEntity(dto);
        existente.setId(1L);
        existente.setGoogleEventId("evento123");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existente));

        taskService.excluir(1L);

        verify(googleCalendarService).excluirEventoTarefa("evento123");
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deveListarTodasTarefas() {
        Task task = TaskMapper.toEntity(dto);
        task.setId(1L);

        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskDto> lista = taskService.listarTodas();

        assertEquals(1, lista.size());
        verify(taskRepository).findAll();
    }

    @Test
    void deveBuscarTarefaPorId() {
        Task task = TaskMapper.toEntity(dto);
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<TaskDto> encontrado = taskService.buscarPorId(1L);

        assertTrue(encontrado.isPresent());
        assertEquals("Tarefa de Teste", encontrado.get().getDescricao());
    }

    @Test
    void deveListarPorStatus() {
        Task task = TaskMapper.toEntity(dto);
        task.setStatus("PENDENTE");

        when(taskRepository.findByStatus("PENDENTE")).thenReturn(List.of(task));

        List<TaskDto> lista = taskService.listarPorStatus("PENDENTE");

        assertEquals(1, lista.size());
        assertEquals("PENDENTE", lista.get(0).getStatus());
    }

    @Test
    void deveListarPorPrioridade() {
        Task task = TaskMapper.toEntity(dto);
        task.setPrioridade("ALTA");

        when(taskRepository.findByPrioridade("ALTA")).thenReturn(List.of(task));

        List<TaskDto> lista = taskService.listarPorPrioridade("ALTA");

        assertEquals(1, lista.size());
        assertEquals("ALTA", lista.get(0).getPrioridade());
    }
}
