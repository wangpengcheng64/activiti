package com.neimeng.workflow.service.process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.neimeng.workflow.command.DeleteTaskCmd;
import com.neimeng.workflow.command.SetFLowNodeAndGoCmd;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.avalon.framework.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 运行中的Task相关的Service
 */
@Slf4j
@Service
@Transactional
public class ProcessTaskService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 完成任务，不设置流程变量
     *
     * @param taskId
     */
    public void completeTask(String taskId) {
        taskService.complete(taskId);
    }

    /**
     * 完成任务，并设置流程变量
     *
     * @param taskId
     * @param variables
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }

    /**
     * 查询指定用户任务
     *
     * @param assignee
     * @return
     */
    public List<Task> getUserTasks(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).orderByTaskCreateTime().asc().list();
    }

    /**
     * 指派用户任务
     *
     * @param taskId
     * @param userId
     */
    public void assigneeTask(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }

    /**
     * 获取历史评论信息
     *
     * @param processInstanceId
     * @return
     */
    public List<Comment> getComments(String processInstanceId) {
        return taskService.getProcessInstanceComments(processInstanceId);
    }

    /**
     * 根据任务id获取任务实例
     *
     * @param taskId
     * @return
     */
    public Task getTaskByTaskId(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 根据流程实例id获取任务实例
     *
     * @param processInstanceId
     * @return
     */
    public List<Task> getTaskByProInstId(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
    }

    /**
     * 获取候选人任务
     *
     * @param candidate
     * @return
     */
    public List<Task> getTasksByCandidate(String candidate) {
        return taskService.createTaskQuery().taskCandidateUser(candidate).orderByTaskCreateTime().desc().list();
    }

    /**
     * 领取任务
     *
     * @param taskId
     * @param userId
     * @return
     */
    public void claimTask(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    /**
     * 获取候选组任务
     *
     * @param candidateGroup
     * @return
     */
    public List<Task> getTasksByCandidateGroup(String candidateGroup) {
        return taskService.createTaskQuery().taskCandidateGroup(candidateGroup).orderByTaskCreateTime().desc().list();
    }

    /**
     * 获取任务数量
     *
     * @param taskAssignee
     * @return
     */
    public long getTaskCount(String taskAssignee) {
        return taskService.createTaskQuery().taskAssignee(taskAssignee).count();
    }

    /**
     * 设置变量
     *
     * @param taskId
     * @param variableName
     * @param value
     */
    public void setVariable(String taskId, String variableName, Object value) {
        taskService.setVariable(taskId, variableName, value);
    }

    /**
     * 根据流程定义key和任务处理人查找相关任务
     *
     * @param assignee
     * @param processDefinitionKey
     * @return
     */
    public List<Task> getTaskCandidateOrAssignedByKey(String assignee, String processDefinitionKey) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(assignee)
                .processDefinitionKey(processDefinitionKey).list();
        return tasks;
    }

    /**
     * Purpose：创建文件类型的附件
     *
     * @param attachmentType        文件类型
     * @param taskId                任务id
     * @param processInstanceId     流程id
     * @param attachmentName        文件名称
     * @param attachmentDescription 文件描述
     * @param content               文件内容
     * @return Attachment
     */
    public Attachment createAttachment(String attachmentType, String taskId, String processInstanceId,
                                       String attachmentName, String attachmentDescription, InputStream content) {
        return taskService.createAttachment(attachmentType, taskId, processInstanceId, attachmentName,
                attachmentDescription, content);
    }

    /**
     * Purpose：修改后保存文件
     *
     * @param attachment
     */
    public void saveAttachment(Attachment attachment) {
        taskService.saveAttachment(attachment);
    }

    /**
     * 删除附件
     *
     * @param attachmentId
     */
    public void deleteAttachment(String attachmentId) {
        taskService.deleteAttachment(attachmentId);
    }

    /**
     * 根据任务id获取相应附件信息
     *
     * @param taskId
     * @return
     */
    public List<Attachment> getTaskAttachments(String taskId) {
        return taskService.getTaskAttachments(taskId);
    }

    /**
     * 根据附件id获取附件信息
     *
     * @param attachmentId
     * @return
     */
    public Attachment getAttachment(String attachmentId) {
        return taskService.getAttachment(attachmentId);
    }

    /**
     * 根据附件id获取附件内容
     *
     * @param attachmentId
     * @return
     */
    public InputStream getAttachmentContent(String attachmentId) {
        return taskService.getAttachmentContent(attachmentId);
    }

    /**
     * 获取流程变量
     *
     * @param taskId
     * @param variableName
     * @return
     */
    public Object getVariable(String taskId, String variableName) {
        return taskService.getVariable(taskId, variableName);
    }

    /**
     * 根据流程定义id获取相关附件信息列表
     *
     * @param processInstanceId
     * @return
     */
    public List<Attachment> getAttachmentByProcessInstanceId(String processInstanceId) {
        return taskService.getProcessInstanceAttachments(processInstanceId);
    }

    /**
     * 删除当前任务相关附件
     *
     * @param taskId
     */
    public void deleteTaskAttachment(String taskId) {
        List<Attachment> attachments = taskService.getTaskAttachments(taskId);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                taskService.deleteAttachment(attachment.getId());
            }
        }
    }

    /**
     * 驳回
     *
     * @param taskId 任务id
     * @throws ServiceException
     */
    public void rejectTask(String taskId, String revokeTaskId) {
        //获取当前任务对象
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (currentTask == null) {
            throw new ActivitiException("当前任务不存在或已被办理完成，回退失败！");
        }
        //获取流程定义id
        String processDefinitionId = currentTask.getProcessDefinitionId();
        //获取bpmn模板
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //获取目标节点定义
        HistoricActivityInstance revokeActInstance = getHistoricActivityInstance(currentTask.getExecutionId(), revokeTaskId);
        FlowNode targetNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(revokeActInstance.getActivityId());
        //删除当前运行任务
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(currentTask.getId()));
        //流程执行到目标节点
        managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    }

    /**
     * 驳回
     *
     * @param currentTaskId 当前任务id
     * @param revokeTaskId 目标id
     * @param remark 原因
     */
    public void rejectTask(String currentTaskId, String revokeTaskId, String remark) {
        final Task currentTask = taskService.createTaskQuery().taskId(currentTaskId).singleResult();
        if (currentTask == null) {
            throw new ActivitiException("当前任务不存在或已被办理完成，回退失败！");
        }
        final String processDefinitionId = currentTask.getProcessDefinitionId();
        final BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        final HistoricActivityInstance revokeActInstance = getHistoricActivityInstance(currentTask.getExecutionId(), revokeTaskId);
        final String revokeElementId = revokeActInstance.getActivityId();

        final FlowNode revokeFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(revokeElementId);
        final Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
        final FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(execution.getActivityId());
        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>(currentFlowNode.getOutgoingFlows());
        //清理活动方向
        currentFlowNode.getOutgoingFlows().clear();
        //建立新方向（当前节点指向回退节点）
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(revokeFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        //向任务/流程实例添加注释
        taskService.addComment(currentTaskId, currentTask.getProcessInstanceId(), remark);
        taskService.addComment(currentTaskId, currentTask.getProcessInstanceId(), revokeElementId);
        //完成任务
        Map<String, Object> currentVariables = Maps.newHashMap();
        taskService.complete(currentTaskId, currentVariables);
        //恢复原方向
        currentFlowNode.setOutgoingFlows(oriSequenceFlows);
        //清除已走过的任务节点信息
        historyService.deleteHistoricTaskInstance(revokeTaskId);
    }

    private HistoricActivityInstance getHistoricActivityInstance(String executionId, String revokeTaskId) {
        List<HistoricActivityInstance> hisList = historyService.createHistoricActivityInstanceQuery().executionId(executionId).finished().list();
        HistoricActivityInstance revokeActInstance = null;
        for (HistoricActivityInstance historicActivityInstance : hisList) {
            if (revokeTaskId.equals(historicActivityInstance.getTaskId())) {
                revokeActInstance = historicActivityInstance;
                break;
            }
        }
        if (revokeActInstance == null) {
            throw new ActivitiException("要指定回滚的taskId=" + revokeTaskId + "节点不存在！");
        }
        return revokeActInstance;
    }
}
