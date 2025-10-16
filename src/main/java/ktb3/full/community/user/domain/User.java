package ktb3.full.community.user.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private Long id;
    private Role role;
    private final String email;
    private String password;
    private String nickname;
    private String profileImage;
    private final LocalDateTime createAt;

    public User(String email, String password, String nickname, String profileImage) {
        this.role = Role.USER;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createAt = LocalDateTime.now();
    }

    public void assignId(Long id){
        this.id = id;
    }

    public void update(String nickname, String profileImage){
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void updatePassword(String password){
        this.password = password;
    }
}
