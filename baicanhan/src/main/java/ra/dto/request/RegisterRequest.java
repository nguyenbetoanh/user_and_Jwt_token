package ra.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RegisterRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String passwords;
    private String address;
    private String state;
    private String city;
    private String post;
    private String phone;
    private String avatar;
    private LocalDate birtDate;
    private boolean statusUser;
    private int ranks;
    private Set<String> listRoles;

}
