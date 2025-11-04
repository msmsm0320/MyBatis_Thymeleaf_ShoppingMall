package com.example.shoppingmall.sercurity;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    /*
    * email을 통해 사용자 조회,
    * email을 통해 찾은 user가 null / user의 enabled 상태가 false / rawPw를 인코딩했을 때, user의 password와 일치 하지 않을경우
    * UNAUTHORIZED Exception 발생
    *  */
    public User authenticate(String email, String rawPw) {
        User u = userMapper.findByEmail(email);
        if (u == null || !u.isEnabled() || !encoder.matches(rawPw, u.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "허가되지 않은 인증입니다.");
        }
        return u;
    }

    /*
     * userId를 통해 해당 user가 가지고 있는 역할(권한) 검색
     */
    public List<String> roles(Long userId){
        return userMapper.findRoleNamesByUserId(userId);
    }

    /*
     * userId를 통해 해당 user가 가지고 있는 refreshToken의 Version을 검색
     */
    public long currentRefreshVersion(Long userId){
        Long v = userMapper.findRefreshVersion(userId);
        return (v == null)? 0L : v;
    }

    /*
     * userId를 통해 해당 user가 가지고 있는 refreshToken의 Version을 검색하여 +1(버전 증가)
     */
    public long bumpRefreshVersion(Long userId){
        long rv = currentRefreshVersion(userId);
        userMapper.upsertRefreshVersion(userId, rv + 1);
        return rv;
    }

    @Transactional
    public void signup(String email, String rawPw, String nickname){
        if(userMapper.findByEmail(email) != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "중복된 이메일입니다.");

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(rawPw));
        user.setNickname(nickname);

        userMapper.insertUser(user);
        userMapper.insertUserRole(user.getId(), "ROLE_USER");
    }

}
