package ra.dto.request;

public class ChangePassword {
    private String userName;
    private String oldPass;
    private String newPass;

    public ChangePassword(String userName, String oldPass, String newPass) {
        this.userName = userName;
        this.oldPass = oldPass;
        this.newPass = newPass;
    }

    public ChangePassword() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
}