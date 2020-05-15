package com.neimeng.workflow.controller.dataset;

import com.github.pagehelper.PageInfo;
import com.neimeng.workflow.entity.Response;
import com.neimeng.workflow.entity.params.ApplyDatasetInfo;
import com.neimeng.workflow.entity.params.ProcessApproval;
import com.neimeng.workflow.entity.pojo.ProcessTask;
import com.neimeng.workflow.entity.query.BasePageQuery;
import com.neimeng.workflow.entity.vo.TaskVo;
import com.neimeng.workflow.service.DatasetProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Api(tags = "数据集流程接口")
@Slf4j
@RestController
@RequestMapping("/process/dataset")
public class DatasetProcessController {

    @Autowired
    private DatasetProcessService datasetProcessService;

    @ApiOperation(value = "申请流程")
    @PostMapping("applyDataSet")
    public Response applyDataSet(@RequestBody @Validated ApplyDatasetInfo datasetBaseInfo, HttpServletRequest request) {
        TaskVo firstTaskVo = datasetProcessService.applyDataSet(datasetBaseInfo, request);
        return Response.success(firstTaskVo);
    }

    @ApiOperation("获取用户需要处理的Task")
    @GetMapping("getUserTask/{userName}")
    public Response getUserTask(BasePageQuery pageQuery, HttpServletRequest request, @PathVariable String userName) {
        PageInfo<TaskVo> pageInfo = datasetProcessService.getUserTask(pageQuery, request, userName);
        return Response.success(pageInfo);
    }

    @ApiOperation("获取候选人任务")
    @GetMapping("getTasksByCandidate/{candidate}")
    public Response getTasksByCandidate(BasePageQuery pageQuery, @PathVariable String candidate) {
        List<Task> taskList = datasetProcessService.getTasksByCandidate(pageQuery, candidate);
        return getResponse(taskList);
    }

    private Response getResponse(List<Task> taskList) {
        List<TaskVo> taskVos = new ArrayList<>();
        taskList.forEach(vo -> {
            TaskVo taskVo = new TaskVo();
            taskVo.setTaskId(vo.getId());
            taskVo.setProcInstId(vo.getProcessInstanceId());
            taskVo.setTaskName(vo.getName());
            taskVos.add(taskVo);
        });
        return Response.success(taskVos);
    }

    @ApiOperation("获取候选组任务")
    @GetMapping("getTasksByCandidateGroup/{candidateGroup}")
    public Response getTasksByCandidateGroup(BasePageQuery pageQuery, @PathVariable String candidateGroup) {
        List<Task> taskList = datasetProcessService.getTasksByCandidateGroup(pageQuery, candidateGroup);
        return getResponse(taskList);
    }

    @ApiOperation("领取任务")
    @GetMapping("claimTask/{taskId}/{userId}")
    public Response claimTask(@PathVariable String taskId, @PathVariable String userId) {
        datasetProcessService.claimTask(taskId, userId);
        return Response.success();
    }

    @ApiOperation("审批任务")
    @PostMapping("approvalTask")
    public Response approvalTask(@RequestBody @Validated ProcessApproval processApproval, HttpServletRequest request) {
        String taskId = datasetProcessService.approvalTask(processApproval, request);
        return Response.success(taskId);
    }

    @ApiOperation("获取审批历史记录")
    @GetMapping("getApprovalHistory")
    public Response getApprovalHistory(String processInstanceId) {
        List<ProcessTask> taskApprovalHisroties = datasetProcessService.getApprovalHistory(processInstanceId);
        return Response.success(taskApprovalHisroties);
    }

    @ApiOperation("终止流程")
    @PostMapping("stopProcess")
    public Response stopProcess(@RequestBody @Validated ProcessApproval processApproval, HttpServletRequest request) {
        datasetProcessService.stopProcess(processApproval, request);
        return Response.success();
    }

    @ApiOperation("驳回流程")
    @GetMapping("rejectTask/{taskId}/{revokeTaskId}/{reason}")
    public Response rejectTask(@PathVariable String taskId, @PathVariable String revokeTaskId, @PathVariable String reason) {
        datasetProcessService.rejectTask(taskId, revokeTaskId, reason);
        return Response.success();
    }

}
