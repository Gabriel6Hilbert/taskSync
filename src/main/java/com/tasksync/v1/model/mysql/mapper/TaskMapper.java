package com.tasksync.v1.model.mysql.mapper;

import com.tasksync.v1.model.mysql.dto.TaskDto;
import com.tasksync.v1.model.mysql.entity.Task;

public class TaskMapper {

    public static TaskDto toDTO(Task entity) {
        return TaskDto.builder()
                .id(entity.getId())
                .descricao(entity.getDescricao())
                .dataHora(entity.getDataHora())
                .status(entity.getStatus())
                .prioridade(entity.getPrioridade())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .responsavel(entity.getResponsavel())
                .concluida(entity.getConcluida())
                .observacoes(entity.getObservacoes())
                .alertaEm(entity.getAlertaEm())
                .linkCalendar(entity.getLink())
                .build();
    }

    public static Task toEntity(TaskDto dto) {
        return Task.builder()
                .id(dto.getId())
                .descricao(dto.getDescricao())
                .dataHora(dto.getDataHora())
                .status(dto.getStatus())
                .prioridade(dto.getPrioridade())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .responsavel(dto.getResponsavel())
                .concluida(dto.getConcluida())
                .observacoes(dto.getObservacoes())
                .alertaEm(dto.getAlertaEm())
                .build();
    }
}
