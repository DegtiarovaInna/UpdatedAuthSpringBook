package com.example.contactManager.controller;

import com.example.contactManager.dto.RoleUpdateRequest;
import com.example.contactManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PutMapping("/user/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest roleUpdateRequest
    ) {
        Long adminId = userService.getUserIdFromToken(token);

        if (!userService.isAdmin(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String role = roleUpdateRequest.getRole();

        if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
            return ResponseEntity.badRequest().build();
        }

        userService.updateUserRole(id, role);
        return ResponseEntity.ok().build();
    }
}

    //2)работает, но администратор может назначать роли пользователям через параметры URL, а не тело)
//    public ResponseEntity<Void> updateUserRole(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestParam String role) {
//        Long adminId = userService.getUserIdFromToken(token); // Получаем ID администратора из токена
//
//        if (!userService.isAdmin(adminId)) { // Проверяем, является ли пользователь администратором
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        userService.updateUserRole(id, role);
//        return ResponseEntity.ok().build();
 //   }
// 1)Изменение: добавлена проверка на права администратора
//    public ResponseEntity<Void> updateUserRole(@PathVariable Long id, @RequestParam String role) {
//        if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
//            return ResponseEntity.badRequest().build();
//        }
//        userService.updateUserRole(id, role);
//        return ResponseEntity.ok().build();
//    }
//    public ResponseEntity<Void> updateUserRole(@PathVariable Long id, @RequestParam String role) {
//        userService.updateUserRole(id, role);
//        return ResponseEntity.ok().build();
//    }
//}