package com.ktdsuniversity.edu.members.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistVO {

	@NotBlank(message="이메일 형태가 아닙니다.")
	@Email(message="이메일 형태가 아닙니다.")
	private String email;

	@NotBlank(message="이름을 입력하세요.")
	@Size(min = 2, max = 4, message="이름은 두글자 이상 네글자 이하로 작성하세요.")
	private String name;

//	@NotEmpty(message="비밀번호를 입력하세요.")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$"
		   , message = "비밀번호는 영소문자, 영대문자, 숫자 최소 1개를 포함하여 8글자 이상으로 입력하세요.")
	private String password;

	private String salt;

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}



}
