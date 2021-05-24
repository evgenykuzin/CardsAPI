package ru.sberbank.kuzin19190813.controller;

import ru.sberbank.kuzin19190813.view.input_pojos.AddUserPOJO;
import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;
import ru.sberbank.kuzin19190813.winter_framework.models.ResponseEntity;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.Controller;
import ru.sberbank.kuzin19190813.winter_framework.rest.annotations.RestMapping;

@Controller(parentPath = "employee")
public class EmployeeController {

    @RestMapping(path = "add-user", method = HttpMethod.POST)
    public ResponseEntity addUser(AddUserPOJO addUserPOJO) {
        return ResponseEntity.getMessageResponse("success", 200);
    }

    @RestMapping(path = "create-account", method = HttpMethod.POST)
    public ResponseEntity createAccount(AddUserPOJO addUserPOJO) {
        return ResponseEntity.getOkResponse();
    }

    @RestMapping(path = "accept-card-creation", method = HttpMethod.POST)
    public ResponseEntity acceptCardCreation(AddUserPOJO addUserPOJO) {
        return ResponseEntity.getOkResponse();
    }

    @RestMapping(path = "accept-operation", method = HttpMethod.POST)
    public ResponseEntity acceptOperation(AddUserPOJO addUserPOJO) {
        return ResponseEntity.getOkResponse();
    }
}
