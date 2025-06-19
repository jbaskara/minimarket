package model;

// Rappresenta un utente del sistema.
public class User {
    private int id;
    private String username;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private boolean admin;

    // Costruttore vuoto.
    public User() {}

    // Costruttore completo per User.
    public User(int id, String username, String nome, String cognome, String email, String password, boolean admin) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    // per OrdiniPanel
    public String getRuolo() {
        return admin ? "admin" : "utente";
    }
}