package com.vti.form;

import com.vti.entity.Department;
import com.vti.validation.AccountUsernameNotExists;
import com.vti.validation.DepartmentNameNotExists;
import lombok.Data;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
public class DepartmentCreateForm {
    @NotBlank(message = "{DepartmentForm.name.NotBlank}")
    @Length(max = 50, message = "Department name has max 50 characters.")
    @DepartmentNameNotExists
    private String name;

    @PositiveOrZero(message = "Department total members must be greatr than or equal to 0.")
    @NotNull(message = "Department total members must be NOT null.")
    private Integer totalMembers;

    @Pattern(
            regexp = "DEVELOPER|TESTER|SCRUM_MASTER|PROJECT_MANAGER",
            message = "Department type must be DEVELOPER, TESTER, SCRUM_MASTER or PROJECT_MANAGER"
    )
    private String type;

    private List<@Valid Account> accounts;
    @Data
    public static class Account{
        @NotBlank(message = "Account username must be NOT blank")
        @Length(max = 50, message = "Account username has max 50 characters.")
        @AccountUsernameNotExists
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String role;
    }
}
