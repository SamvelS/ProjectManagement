package com.workfront.ProjectManagement.repositoriy.impl.hibernate;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import com.workfront.ProjectManagement.repositoriy.TaskRepository;
import com.workfront.ProjectManagement.utilities.Constants;
import com.workfront.ProjectManagement.utilities.HibernateUtil;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "dbType", havingValue = "hibernate")
public class HibernateTaskRepository implements TaskRepository {
    @Autowired
    private ActionStatusRepository actionStatusRepository;

    @Override
    public List<Task> getTasksInfo(int from, int count, int projectId, int userId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            String queryText = "select t from Task t";

            Map<String, Object> params = new HashMap<>();

            boolean isForSpecificUser = (userId != Constants.getAllUsersId());
            if(isForSpecificUser) {
                queryText += " inner join t.assignees a where a.id =:userId and t.projectId=:projectId";
                params.put("userId", userId);
            }
            else {
                queryText += " where t.projectId=:projectId";
            }
            params.put("projectId", projectId);

            queryText += " order by t.id";

            Query query = session.createQuery(queryText, Task.class);

            for (String key:
                 params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            query.setFirstResult(from);
            query.setMaxResults(count);

            List<Task> tasks = query.getResultList();

            List<ActionStatus> actionStatuses = this.actionStatusRepository.getActionStatuses();

            tasks.forEach(t -> {
                t.getCreatedBy().setPassword("");
                t.getAssignees().forEach(a -> a.setPassword(""));

                this.setTaskStatus(t, actionStatuses);
            });

            return tasks;
        }
    }

    @Override
    public List<Task> getAllTasksInfo(int projectId, int userId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            String queryText = "select t from Task t";

            Map<String, Object> params = new HashMap<>();

            boolean isForSpecificUser = (userId != Constants.getAllUsersId());
            if(isForSpecificUser) {
                queryText += " inner join t.assignees a where a.id =:userId and t.projectId=:projectId";
                params.put("userId", userId);
            }
            else {
                queryText += " where t.projectId=:projectId";
            }
            params.put("projectId", projectId);

            queryText += " order by t.id";

            Query query = session.createQuery(queryText, Task.class);

            for (String key:
                    params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            List<Task> tasks = query.getResultList();

            List<ActionStatus> actionStatuses = this.actionStatusRepository.getActionStatuses();

            tasks.forEach(t -> {
                t.getCreatedBy().setPassword("");
                t.getAssignees().forEach(a -> a.setPassword(""));

                this.setTaskStatus(t, actionStatuses);
            });

            return tasks;
        }
    }

    @Override
    public Task getTaskDetails(int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Task task = session.get(Task.class, id);
            if(task != null) {
                task.getCreatedBy().setPassword("");
                task.getAssignees().forEach(a -> a.setPassword(""));
                this.setTaskStatus(task, this.actionStatusRepository.getActionStatuses());
            }

            return task;
        }
    }

    @Override
    public Task getTaskInfo(int id) {
        return this.getTaskDetails(id);
    }

    @Override
    public int getTasksCount(int projectId, int userId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "select count(id) from Task ");
            return ((Long)((org.hibernate.query.Query) query).uniqueResult()).intValue();
        }
    }

    @Override
    public void createTask(Task task) {

    }

    @Override
    public void editTask(Task task) {

    }

    @Override
    public void deleteTaskById(int id) {

    }

    private void setTaskStatus(Task task, List<ActionStatus> actionStatuses) {
        String status = actionStatuses.stream().filter(s -> s.getId() == Constants.getNotStartedActionStatusId()).findFirst().get().getName();

        if(task.getAssignees() != null && !task.getAssignees().isEmpty()) {
            if (task.getAssignees().stream().anyMatch(a -> a.getStatusId() == Constants.getInProgressActionStatusId())) {
                status = actionStatuses.stream().filter(s -> s.getId() == Constants.getInProgressActionStatusId()).findFirst().get().getName();
            }
            else if(task.getAssignees().stream().allMatch(a -> a.getStatusId() == Constants.getCompletedActionStatusId())) {
                status = actionStatuses.stream().filter(s -> s.getId() == Constants.getCompletedActionStatusId()).findFirst().get().getName();
            }
        }
        task.setStatus(status);
    }
}
