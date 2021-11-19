package di.uoa.gr.dira.services.projectService;

import di.uoa.gr.dira.entities.customer.Customer;
import di.uoa.gr.dira.entities.issue.Issue;
import di.uoa.gr.dira.entities.project.Permission;
import di.uoa.gr.dira.entities.project.Project;
import di.uoa.gr.dira.entities.sprint.Sprint;
import di.uoa.gr.dira.exceptions.commonExceptions.ActionNotPermittedException;
import di.uoa.gr.dira.exceptions.customer.CustomerNotFoundException;
import di.uoa.gr.dira.exceptions.project.ProjectAlreadyExistsException;
import di.uoa.gr.dira.exceptions.project.ProjectNotFoundException;
import di.uoa.gr.dira.exceptions.project.permission.PermissionNotFoundException;
import di.uoa.gr.dira.models.project.ProjectModel;
import di.uoa.gr.dira.models.project.ProjectUsersModel;
import di.uoa.gr.dira.repositories.*;
import di.uoa.gr.dira.services.BaseService;
import di.uoa.gr.dira.services.permissionService.IPermissionService;
import di.uoa.gr.dira.shared.PermissionType;
import di.uoa.gr.dira.shared.PermissionTypeEnum;
import di.uoa.gr.dira.shared.ProjectVisibility;
import di.uoa.gr.dira.shared.SubscriptionPlanEnum;
import di.uoa.gr.dira.util.mapper.MapperHelper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService extends BaseService<ProjectModel, Project, Long, ProjectRepository> implements IProjectService {
    private final static int STANDARD_MEMBER_LIMIT = 5;
    private final static int PREMIUM_MEMBER_LIMIT = 15;

    CustomerRepository customerRepository;
    IssueRepository issueRepository;
    SprintRepository sprintRepository;
    IPermissionService permissionService;

    public ProjectService(
            ProjectRepository repository,
            CustomerRepository customerRepository,
            IssueRepository issueRepository,
            SprintRepository sprintRepository,
            IPermissionService permissionService,
            ModelMapper mapper) {
        super(repository, mapper);
        this.customerRepository = customerRepository;
        this.permissionService = permissionService;
        this.issueRepository = issueRepository;
        this.sprintRepository = sprintRepository;
    }

    private Project checkPermissions(Long projectId, Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("customerId", customerId.toString()));

        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("projectId", projectId.toString()));

        if (!permissionService.checkProjectUserPermissions(customerId, project, PermissionType.ADMIN)) {
            throw new ActionNotPermittedException("You need ADMIN permissions");
        }

        return project;
    }

    @Override
    public List<ProjectModel> findAllPublicProjects() {
        List<Project> publicProjects = repository.findAll()
                .stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC)
                .collect(Collectors.toList());

        return MapperHelper.mapList(mapper, publicProjects, modelType);
    }

    @Override
    public ProjectModel createProject(Long customerId, ProjectModel projectModel) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("customerId", customerId.toString()));

        repository.findByKey(projectModel.getKey())
                .ifPresent(project -> {
                    throw new ProjectAlreadyExistsException("projectId", project.getId().toString());
                });

        if ((customer.getSubscriptionPlan().getPlan().equals(SubscriptionPlanEnum.STANDARD)) && (projectModel.getVisibility().equals(ProjectVisibility.PRIVATE))) {
            throw new ActionNotPermittedException();
        }

        Project project = mapper.map(projectModel, entityType);

        // adding customer who created the project
        project.setCustomers(new ArrayList<>());
        project.getCustomers().add(customer);
        project.setPermissions(new ArrayList<>());
        project = repository.save(project);

        /* Create a new permission for this customer in the current project */
        Permission permission = new Permission();
        permission.setProject(project);
        permission.setUser(customer);
        permission.setPermission(PermissionType.ADMIN.getPermission());
        permissionService.getRepository().save(permission);

        return mapper.map(project, ProjectModel.class);
    }

    @Override
    public ProjectModel getProject(Long projectId, SubscriptionPlanEnum subscriptionPlan) {
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("projectId", projectId.toString()));

        if (project.getVisibility().equals(ProjectVisibility.PRIVATE) && subscriptionPlan.equals(SubscriptionPlanEnum.STANDARD)) {
            throw new ActionNotPermittedException();
        }

        return mapper.map(project, modelType);
    }

    @Override
    public ProjectModel updateProjectWithId(Long projectId, Long customerId, ProjectModel projectModel) {
        Project project = checkPermissions(projectId, customerId);
        mapper.map(projectModel, project);
        project = repository.save(project);
        return mapper.map(project, modelType);
    }

    @Override
    public void deleteProjectWithId(Long projectId, Long customerId) {
        Project project = checkPermissions(projectId, customerId);
        for (Customer customer : project.getCustomers()) {
            customer.getProjects().remove(project);
        }
        for (Issue issue : project.getIssues()) {
            issueRepository.delete(issue);
        }
        for (Sprint sprint : project.getSprints()) {
            sprintRepository.delete(sprint);
        }
        for (Permission permission : project.getPermissions()) {
            permissionService.getRepository().delete(permission);
        }
        repository.delete(project);
    }

    /* ProjectUserController */

    @Override
    public ProjectUsersModel findUsersByProjectId(Long id) {
        return repository.findById(id)
                .map(project -> mapper.map(project, ProjectUsersModel.class))
                .orElseThrow(() -> new ProjectNotFoundException("projectId", id.toString()));
    }

    @Override
    public void addUserToProjectWithId(Long projectId, Long inviterId, Long inviteeId) {
        Project project = checkPermissions(projectId, inviterId);
        Customer customer = customerRepository.findById(inviteeId)
                .orElseThrow(() -> new CustomerNotFoundException("userId", inviteeId.toString()));

        project.getCustomers().add(customer);
        repository.save(project);

        permissionService.createProjectUserPermission(inviterId, project, customer, PermissionTypeEnum.READ);
    }

    @Override
    public void addUserToProjectWithEmail(Long projectId, Long inviterId, String email) {
        Project project = checkPermissions(projectId, inviterId);
        Customer inviter = customerRepository.findById(inviterId).get();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("email", email));

        if (project.getCustomers().contains(customer)) {
            throw new ActionNotPermittedException();
        }

        if (inviter.getSubscriptionPlan().getPlan().equals(SubscriptionPlanEnum.STANDARD) && project.getCustomers().size() == STANDARD_MEMBER_LIMIT) {
            throw new ActionNotPermittedException();
        }
        else if (inviter.getSubscriptionPlan().getPlan().equals(SubscriptionPlanEnum.PREMIUM) && project.getCustomers().size() == PREMIUM_MEMBER_LIMIT) {
            throw new ActionNotPermittedException();
        }

        project.getCustomers().add(customer);
        repository.save(project);

        permissionService.createProjectUserPermission(inviterId, project, customer, PermissionTypeEnum.READ);
    }

    @Override
    public void deleteUserFromProjectWithId(Long id, Long projectOwnerId, Long userId) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new CustomerNotFoundException("userId", userId.toString()));

        Project project = checkPermissions(id, projectOwnerId);

        if (!project.getCustomers().contains(customer)) {
            throw new CustomerNotFoundException(
                    String.format("Customer %s was not found in project %s", customer.getName(), project.getName())
            );
        }
        project.getCustomers().remove(customer);
        repository.save(project);

        Permission permission = permissionService.getRepository()
                .findByUserId(customer.getId())
                .orElseThrow(() -> new PermissionNotFoundException(String.format("Permission for user %s not found", customer.getName())));

        permissionService.getRepository().delete(permission);
    }
}