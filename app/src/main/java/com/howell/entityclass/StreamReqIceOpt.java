package com.howell.entityclass;

public class StreamReqIceOpt {
    private int comp_cnt;
    private String stun_addr;
    private int stun_port;
    private String turn_addr;
    private int turn_port;
    private int turn_tcp;
    private String turn_username;
    private String turn_password;

    public StreamReqIceOpt(int comp_cnt, String stun_addr, int stun_port,
            String turn_addr, int turn_port, int turn_tcp,
            String turn_username, String turn_password) {
        super();
        this.comp_cnt = comp_cnt;
        this.stun_addr = stun_addr;
        this.stun_port = stun_port;
        this.turn_addr = turn_addr;
        this.turn_port = turn_port;
        this.turn_tcp = turn_tcp;
        this.turn_username = turn_username;
        this.turn_password = turn_password;
    }

    public int getComp_cnt() {
        return comp_cnt;
    }

    public void setComp_cnt(int comp_cnt) {
        this.comp_cnt = comp_cnt;
    }

    public String getStun_addr() {
        return stun_addr;
    }

    public void setStun_addr(String stun_addr) {
        this.stun_addr = stun_addr;
    }

    public int getStun_port() {
        return stun_port;
    }

    public void setStun_port(int stun_port) {
        this.stun_port = stun_port;
    }

    public String getTurn_addr() {
        return turn_addr;
    }

    public void setTurn_addr(String turn_addr) {
        this.turn_addr = turn_addr;
    }

    public int getTurn_port() {
        return turn_port;
    }

    public void setTurn_port(int turn_port) {
        this.turn_port = turn_port;
    }

    public int getTurn_tcp() {
        return turn_tcp;
    }

    public void setTurn_tcp(int turn_tcp) {
        this.turn_tcp = turn_tcp;
    }

    public String getTurn_username() {
        return turn_username;
    }

    public void setTurn_username(String turn_username) {
        this.turn_username = turn_username;
    }

    public String getTurn_password() {
        return turn_password;
    }

    public void setTurn_password(String turn_password) {
        this.turn_password = turn_password;
    }

}
