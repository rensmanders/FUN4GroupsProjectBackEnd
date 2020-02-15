package App.controllers.department;

import App.controllers.enums.DepartmentResponse;
import App.entity.Department;
import App.models.department.DepartmentRegisterModel;
import App.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/department")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity getDepartments() {
        List<Department> departments = this.departmentService.findAll();

        if(departments.isEmpty()) {
            return new ResponseEntity<>(DepartmentResponse.NO_DEPARTMENTS.toString(), HttpStatus.BAD_REQUEST);
        }

        return ok(departments);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity getDepartment(@Valid @PathVariable String id) {
        Optional<Department> department = this.departmentService.findById(UUID.fromString(id));

        if(!department.isPresent()) {
            return new ResponseEntity<>(DepartmentResponse.NO_DEPARTMENT.toString(), HttpStatus.BAD_REQUEST);
        }

        return ok(department);
    }

    @PostMapping
    public ResponseEntity createDepartment(@Valid @RequestBody DepartmentRegisterModel departmentName) {
        if(this.departmentService.findDepartmentByDepartmentName(departmentName.getDepartmentName()).isPresent()) {
            return new ResponseEntity<>(DepartmentResponse.ALREADY_EXISTS.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Department department = new Department(departmentName.getDepartmentName());
            this.departmentService.createOrUpdate(department);

            return ok(department);
        } catch(Exception ex) {
            return new ResponseEntity<>(DepartmentResponse.ERROR.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity deleteDepartment(@Valid @PathVariable String id) {
        Optional<Department> department = this.departmentService.findById(UUID.fromString(id));

        if(department.isPresent()) {
            try {
                this.departmentService.delete(department.get());
                return new ResponseEntity<>(DepartmentResponse.SUCCESSFULLY_DELETED.toString(), HttpStatus.OK);
            } catch(Exception ex) {
                return new ResponseEntity<>(DepartmentResponse.ERROR.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(DepartmentResponse.NO_DEPARTMENT.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
