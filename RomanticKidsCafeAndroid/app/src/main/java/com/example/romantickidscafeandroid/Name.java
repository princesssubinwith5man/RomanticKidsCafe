package com.example.romantickidscafeandroid;

public class Name {
    String name; //이름
    String email; //이메일

    public Name() {
    } //이건 기본적으로 쓰더라구요.

    public Name(String toString, String toString1) {

    }


    //get, set 함수는 커스텀 리스트 뷰를 사용하시는 분들과.. 필요하신 분만 작성하시면 좋습니다.
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getemail() {
        return email;
    }

    public void setkind(String email) {
        this.email = email;
    }
    public Name(String name){
        this.name = name;
    }

}