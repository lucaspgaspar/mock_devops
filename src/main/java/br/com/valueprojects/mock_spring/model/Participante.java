package br.com.valueprojects.mock_spring.model;

public class Participante {

    private int id;
    private String nome;
    private String Celular;

    public Participante(String nome) {
        this(0, nome, "1111-1111");
    }

    public Participante(int id, String nome, String celular) {
        this.id = id;
        this.nome = nome;
        this.Celular = celular;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Participante other = (Participante) obj;
        if (id != other.id)
            return false;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        return true;
    }


    public String getCelular() {
        return this.Celular;
    }
}
