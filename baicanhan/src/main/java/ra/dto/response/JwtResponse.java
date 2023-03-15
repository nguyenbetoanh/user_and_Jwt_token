package ra.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private int userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String state;
    private String city;
    private String post;
    private String phone;
    private String avatar;
    private LocalDate birtDate;
    private boolean statusUser;
    private int ranks;
    private List<String> listRoles;
    private String token;

    private String type = "Bearer";
    public JwtResponse(int userId,String firstName, String lastName, String token, String userName, String email, String address, String state, String city,
                       String post, String phone, String avatar, int ranks, List<String> listRoles) {
        this.userId=userId;
        this.firstName=firstName;
        this.lastName=lastName;
        this.token = token;
        this.userName=userName;
        this.email=email;
        this.address=address;
        this.state=state;
        this.city=city;
        this.phone=phone;
        this.post=post;
        this.avatar=avatar;
        this.ranks=ranks;
        this.listRoles=listRoles;
    }
}








