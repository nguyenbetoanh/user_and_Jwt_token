package ra.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ra.dto.request.ChangePassword;
import ra.dto.request.LoginRequest;
import ra.dto.request.RegisterRequest;
import ra.dto.response.JwtResponse;
import ra.dto.response.MessageResponse;
import ra.jwt.JwtTokenProvider;
import ra.model.entity.*;
import ra.model.service.RoleService;
import ra.model.service.UserService;
import ra.model.serviceImp.UserServiceImp;
import ra.security.CustomUserDetails;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    @Autowired
    private UserServiceImp userServiceImp;

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private UserService userService;
    private PasswordEncoder encoder;
    private RoleService roleService;


    //    ------------------    ĐĂNG KÝ   ----------------------
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) throws Throwable {
        if (userService.existsByUserName(registerRequest.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already"));
        }
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));
        }
        Users user = new Users();
        user.setUserName(registerRequest.getUserName());
        user.setPasswords(encoder.encode(registerRequest.getPasswords()));
        user.setAvatar(registerRequest.getAvatar());
        user.setLastName(registerRequest.getLastName());
        user.setFirstName(registerRequest.getFirstName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setState(registerRequest.getState());
        user.setCity(registerRequest.getCity());
        user.setPost(registerRequest.getPost());
        user.setBirtDate(registerRequest.getBirtDate());
        user.setRanks(0);
        user.setStatusUser(true);
        Set<String> strRoles = registerRequest.getListRoles();
        Set<Roles> listRoles = new HashSet<>();
        if (strRoles == null) {
            //User quyen mac dinh
            Roles userRole = (Roles) roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            listRoles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Roles adminRole = null;
                        try {
                            adminRole = (Roles) roleService.findByRoleName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(adminRole);
                    case "moderator":
                        Roles modRole = null;
                        try {
                            modRole = (Roles) roleService.findByRoleName(ERole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(modRole);
                    case "user":
                        Roles userRole = null;
                        try {
                            userRole = (Roles) roleService.findByRoleName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        listRoles.add(userRole);
                }
            });
        }
        user.setListRoles(listRoles);
        userServiceImp.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User registered successful"));
    }


    //    ------------------    ĐĂNG NHẬP  ----------------------

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> loginUser(@RequestBody LoginRequest userLogin) {
        Users users = userService.findUsersByUserName(userLogin.getUserName());
        if (users.isStatusUser()) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getUserName(), userLogin.getPasswords())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();
            //Sinh JWT tra ve client
            String jwt = tokenProvider.generateToken(customUserDetail);
            //Lay cac quyen cua user
            List<String> listRoles = customUserDetail.getAuthorities().stream()
                    .map(item -> item.getAuthority()).collect(Collectors.toList());
            JwtResponse response = new JwtResponse(customUserDetail.getUserId(), customUserDetail.getFirstName(), customUserDetail.getLastName(), jwt, customUserDetail.getUsername(), customUserDetail.getEmail(),
                    customUserDetail.getAddress(), customUserDetail.getState(), customUserDetail.getCity(), customUserDetail.getPost(), customUserDetail.getPhone(), customUserDetail.getAvatar(), customUserDetail.getRanks(), listRoles);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Welcome " + userLogin.getUserName(), response));
        } else {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseObject("ok", "Tài khoản " + userLogin.getUserName() + " của bạn đã bị khóa ! Vui lòng liên hệ với admin ", ""));
        }

    }

    @DeleteMapping("/block_user/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable int userId) {
        try {
            CustomUserDetails customUserDetail = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users users = (Users) userService.findById(userId);
            if (customUserDetail.getAuthorities().size() > users.getListRoles().size()) {
                users.setStatusUser(false);
                userService.saveOrUpdate(users);
            }
            return ResponseEntity.ok("BlockUser successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("BlockUser Error", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/logOut")
    public ResponseEntity<?> logOut() {
        // Clear the authentication from server-side (in this case, Spring Security)
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/getAll")
    public List<Users> getAll() {
        return userServiceImp.getAll();
    }

    @PutMapping("  /{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateUserForModerator(@PathVariable("userId") int userId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users userUpdateModerator = (Users) userService.findById(userId);
        if (userDetails.getAuthorities().size() > userUpdateModerator.getListRoles().size()) {
            userUpdateModerator.setStatusUser(false);
            userService.saveOrUpdate(userUpdateModerator);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Đã cập nhật trạng thái và rank cho " + userUpdateModerator.getUserName(), userUpdateModerator));

        } else {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseObject("false", "Bạn không đủ quyền cập nhật cho " + userUpdateModerator.getUserName(), "Vẫn ko thay đổi"));
        }
    }

    @PostMapping("changePassword")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePassword changePassword) {
//        USER dang dang nhap
        CustomUserDetails usersChangePass = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.findUsersByUserName(usersChangePass.getUsername());
//        thong tin request
        String userName = changePassword.getUserName();
        String oldPass = changePassword.getOldPass();
        String newPass = changePassword.getNewPass();

        if (usersChangePass.getUsername().equals(userName) && BCrypt.checkpw(oldPass, usersChangePass.getPassword())) {
            users.setPasswords(encoder.encode(newPass));
            userService.saveOrUpdate(users);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Đã cập nhật thành công tài khoản  " + usersChangePass.getUsername(), ""));
        }else {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseObject("false", "Không cập nhật được tài khoản  " + usersChangePass.getUsername(), ""));
        }
    }
}