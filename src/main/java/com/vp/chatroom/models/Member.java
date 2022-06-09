package com.vp.chatroom.models;

import java.util.Objects;

public class Member {
	private String memberName;

	public Member() {}

	public Member(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Member member = (Member) o;
		return memberName.equals(member.memberName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(memberName);
	}

	@Override
	public String toString() {
		return "Member: {" +
				"memberName=" + memberName +
				"}";
	}
}
