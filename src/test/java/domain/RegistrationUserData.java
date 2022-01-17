package domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegistrationUserData {
    private final String login;
    private final String password;
    private final String status;
}
/*  Если не использовать аннотацию Data и RequiredArgsConstructor lombok, то:
  public class UserData {
    private final String login;
    private final String password;
    private final String status;

    public UserData (String login, String password, String status) {
       this.login = login;
       this.password = password;
       this.status = status;
    }

    public String getLogin() { return login; }

    public String getPassword() { return password; }

    public String getStatus() { return status; }
  }
 */