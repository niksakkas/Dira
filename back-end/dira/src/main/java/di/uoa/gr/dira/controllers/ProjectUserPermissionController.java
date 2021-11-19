package di.uoa.gr.dira.controllers;


import di.uoa.gr.dira.exceptions.project.permission.PermissionNotFoundException;
import di.uoa.gr.dira.models.project.permission.ProjectUserPermissionModel;
import di.uoa.gr.dira.security.JwtHelper;
import di.uoa.gr.dira.services.permissionService.IPermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("projects/{projectId}/users/permissions")
public class ProjectUserPermissionController {
    private final IPermissionService service;
    private final JwtHelper jwtHelper;

    public ProjectUserPermissionController(IPermissionService service, JwtHelper jwtHelper) {
        this.service = service;
        this.jwtHelper = jwtHelper;
    }

    @ApiOperation(
            value = "Retrieves all the user permissions for the project with the given id",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping
    public List<@Valid ProjectUserPermissionModel> getAllProjectUserPermissions(@PathVariable Long projectId) {
        return service.getAllProjectUserPermissions(projectId);
    }

    @ApiOperation(
            value = "Creates a new user permission for the project with the given id",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @Valid ProjectUserPermissionModel createProjectUserPermission(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUserPermissionModel userPermissionModel,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        Long customerId = jwtHelper.getId(jwtToken);
        return service.createProjectUserPermission(customerId, projectId, userPermissionModel);
    }

    @ApiOperation(
            value = "Updates the user permission for the project with the given id",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PutMapping("{permissionId}")
    public @Valid ProjectUserPermissionModel updateProjectUserPermission(
            @PathVariable Long projectId,
            @PathVariable Long permissionId,
            @Valid @RequestBody ProjectUserPermissionModel userPermissionModel,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        Long customerId = jwtHelper.getId(jwtToken);
        return service.updateProjectUserPermission(customerId, projectId, permissionId, userPermissionModel);
    }

    @ApiOperation(
            value = "Deletes the user's permission for the project with the given id"
    )
    @DeleteMapping("{permissionId}")
    public void deleteProjectUserPermission(
            @PathVariable Long projectId,
            @PathVariable Long permissionId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        Long customerId = jwtHelper.getId(jwtToken);
        service.deleteProjectUserPermission(customerId, projectId, permissionId);
    }

    @GetMapping("{id}")
    public @Valid ProjectUserPermissionModel getProjectUserPermissionById(
            @PathVariable Long projectId,
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        Long customerId = jwtHelper.getId(jwtToken);

        return service.getProjectUserPermissionById(customerId, projectId, id)
                .orElseThrow(() -> new PermissionNotFoundException("id", id.toString()));
    }
}
