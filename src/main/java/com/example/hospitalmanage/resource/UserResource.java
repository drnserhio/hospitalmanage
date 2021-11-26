package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.model.HttpResponse;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.UserPrincipal;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.util.JwtTokenProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.hospitalmanage.constant.FileConstant.*;
import static com.example.hospitalmanage.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
@AllArgsConstructor
public class UserResource extends ExceptionHandling {

    public static final String USER_DELETE_SUCCESSFULLY = "User delete successfully";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user)
            throws MessagingException, UserNotFoundException, UserNameExistsException, EmailExistsException {
       User newUser = userService.register(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
       );
       return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestBody User user) {
        authentificated(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeaders, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam("isActive") String isActive,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException {
        User newUser = userService.addNewUser(
                firstname,
                lastname,
                username,
                email,
                password,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(newUser, OK);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(
           @RequestParam("currentUsername") String currentUsername,
           @RequestParam("firstname") String firstname,
           @RequestParam("lastname") String lastname,
           @RequestParam("username") String username,
           @RequestParam("email") String email,
           @RequestParam("role") String role,
           @RequestParam("isNonLocked") String isNonLocked,
           @RequestParam("isActive") String isActive,
           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User updateUser = userService.updateUser(
                currentUsername,
                firstname,
                lastname,
                username,
                email,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(updateUser, OK);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<User> updateProfile(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam(value = "firstname", required = false) String firstname,
            @RequestParam(value = "lastname", required = false) String lastname,
            @RequestParam(value = "patronomic", required = false) String patronomic,
            @RequestParam(value = "age", required = false) String age,
            @RequestParam(value = "username",required = false) String username,
            @RequestParam(value = "email", required = false) String email,
//            @RequestParam("password") String password,
            @RequestParam(value = "QRCODE", required = false) String QRCODE,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "infoAboutComplaint", required = false) String infoAboutComplaint,
            @RequestParam(value = "infoAboutSick", required = false) String infoAboutSick
    ) throws MessagingException {
        User user = userService.updateProfile(
                currentUsername,
                firstname,
                lastname,
                patronomic,
                age,
                username,
                email,
                QRCODE,
                address,
                infoAboutComplaint,
                infoAboutSick);
        return new ResponseEntity<>(user, OK);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(
            @PathVariable("id") long id) {
        userService.deleteUser(id);
        return response(NO_CONTENT, USER_DELETE_SUCCESSFULLY);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User userByUsername = userService.findUserByUsername(username);
        return new ResponseEntity<>(userByUsername, OK);
    }

    @GetMapping(path = "image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(
            @PathVariable("username") String username)
            throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) > 0) {
                byteArrayOutputStream.write(bytes, 0 , read);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping(path = "image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @PathVariable("username") String username,
            @PathVariable("profileImage") String filename) throws IOException {

        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User user = userService.updateProfileImage(username, profileImage);
     return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getRoleUser();
        return new ResponseEntity<>(users, OK);
    }



    ///------->>>> Doctor

//    @PutMapping("/update/patient")
//    public ResponseEntity<User> changeInformationAboutStateUser(
//            @RequestParam("currentUsername") String currentUsername,
//            @RequestParam(value = "investigationAboutBody", required = false) String investigationAboutBody,
//            @RequestParam(value = "treatment", required = false) String treatment,
//            @RequestParam(value = "gospitalization", required = false) String gospitalization) {
//
//    }


    ///------->>>>

    ///----->>>> Secretary
    @PutMapping("/updatetime")
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    public ResponseEntity<User> updateUserTimeVisitByUsername(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("timeVisit")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss") LocalDateTime timeVisit)
            throws UserNotFoundException {
        User updateUser = userService.updateUserTimeVisitByUsername(currentUsername, timeVisit);
        return new ResponseEntity<>(updateUser, OK);
    }

    ////------>>>>>>>


   ///------->>>> User

    @PutMapping("/changepass")
    @PreAuthorize("hasAnyAuthority('user:change-pass')")
    public ResponseEntity<User> changePassByUsernameAndOldPassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        User user = userService.changePassByUsernameAndOldPassword(oldPassword, newPassword);
        return new ResponseEntity<>(user, OK);
    }


    /////---->>>>>

    private void authentificated(String username, String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(),
                        httpStatus,
                        httpStatus.getReasonPhrase().toUpperCase(),
                        message.toUpperCase()),
                        httpStatus);
    }

}
