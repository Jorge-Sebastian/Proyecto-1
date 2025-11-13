package Boletamaster;

public abstract class Usuario {
    protected String id;
    protected String login;
    protected String password;

    public Usuario(String id, String login, String password) {
        this.id = id; this.login = login; this.password = password;
    }
    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
}