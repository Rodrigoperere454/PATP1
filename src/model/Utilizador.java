package model;

public class Utilizador {
    private int id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String type;

    public Utilizador(String name, String username, String password, String email, String type) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getPassword() { return password; }
}
