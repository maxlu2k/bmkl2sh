package com.demo.services;

import com.demo.dto.request.AuthenticationRequest;
import com.demo.dto.request.IntrospectRequest;
import com.demo.dto.request.LogoutRequest;
import com.demo.dto.response.AuthenticationResponse;
import com.demo.dto.response.IntrospectResponse;
import com.demo.entities.BackListToken;
import com.demo.entities.User;
import com.demo.repositories.BlackListTokenRepository;
import com.demo.repositories.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlackListTokenRepository blackListTokenRepository;

    @Autowired
    DynamicBlacklistScheduler dynamicBlacklistScheduler;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    //tạo phương thức để verify token
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
            try {
                verifyToken(token);
            }catch (Exception e){
                isValid = false;
            }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("domain.vn")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli() // Refresh Token hết hạn sau 7 ngày
                ))
                .claim("type", "refresh_token") // Đánh dấu loại token
                .claim("scope", buildScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaimsSet.toJSONObject()));

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error creating Refresh Token", e);
        }
    }

    private String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        //để build 1 payload thì cần 1 claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                //define nhưng claim cơ bản tiêu chuẩn ở đây
                .subject(user.getUsername()) //username của người đăng nhập
                .issuer("domain.vn")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli() //time hết hạn
                ))
                .claim("type", "access_token")
                .claim("scope", buildScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();

        //sau khi build claim xong thì sẽ tạo payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        // tham số gồm header và payload
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes())); //ký một chữ ký
            return jwsObject.serialize(); //trả về một chuỗi jwsobject (serialize() để chuyển đổi thành chuỗi có thể lưu trữ được)
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    public String refreshAccessToken(String refreshToken) throws JOSEException, ParseException {
        // Giải mã và xác thực Refresh Token
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        boolean isVerified = signedJWT.verify(verifier);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        verifyToken(refreshToken);
        if (!isVerified || expirationTime.before(new Date())) {
            throw new RuntimeException("Invalid or expired Refresh Token");
        }

        // Lấy username từ Refresh Token
        String username = signedJWT.getJWTClaimsSet().getSubject();

        // Tìm user trong cơ sở dữ liệu
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Tạo mới Access Token
        return generateAccessToken(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Not found username: " + request.getUsername()));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new RuntimeException("UnAuthenticated");
        }
        if (!user.getIsActive()) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .message("Account is inactive. Please contact support.")
                    .build();
        }

        var accessToken = generateAccessToken(user); // Tạo Access Token
        var refreshToken = generateRefreshToken(user); // Tạo Refresh Token

        //xong hàm generateToken và nếu authenticated thành công thì sẽ generateToken
        var token = generateAccessToken(user);
        return AuthenticationResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .authenticated(true)
                .build();
    }


    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expryTime = signToken.getJWTClaimsSet().getExpirationTime();
        LocalDateTime expiryDateTime = expryTime.toInstant()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDateTime();
        BackListToken blackListToken = BackListToken.builder()
                .id(jit)
                .expiration(Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        blackListTokenRepository.save(blackListToken);
        dynamicBlacklistScheduler.scheduleTokenDeletion(jit,expiryDateTime);
    }

    //verifyToken
    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        //mã hoá bằng thuật toán nào thì dùng thuật toán ấy để verify
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        //parse token từ chuỗi token nhận được
        SignedJWT signedJWT = SignedJWT.parse(token);
        //lấy thời gian hết hạn
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        //bước verify
        var verified = signedJWT.verify(verifier);
        if(!verified && expityTime.after(new Date())){
            throw new RuntimeException("error from verify");
        }

        //kiểm tra id token có tồn tại trong blacklist hay ko nếu ko có thì sẽ đi tiếp còn nếu có sẽ chặn lại với exceptiom
        if(blackListTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new RuntimeException("NOT FOUND ID!");
        }
        return signedJWT;
    }

    private String buildScope(User user){
        // vì scope là một list nên sử dụng StringJoiner để gộp nó vào thành chuỗi
        StringJoiner stringJoiner = new StringJoiner(" ");
        //collectionUtils kiểm tra xem userRole có empty hay không
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles() .forEach(role -> {
                stringJoiner.add(role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }
        return stringJoiner.toString();
    }
}
