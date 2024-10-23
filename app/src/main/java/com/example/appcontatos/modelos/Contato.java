package com.example.appcontatos.modelos;

import java.util.ArrayList;
import java.util.List;

public class Contato {

    private int id;
    private String nome;
    private List<String[]> telefones; // Lista de arrays [tipo, numero]

    public Contato(int id, String nome, List<String[]> telefones) {
        this.id = id;
        this.nome = nome;
        this.telefones = telefones;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<String[]> getTelefones() {
        return telefones;
    }

    // Método para adicionar um novo telefone
    public void addTelefone(String tipo, String numero) {
        telefones.add(new String[]{tipo, numero});
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefones(List<String[]> telefones) {
        this.telefones = telefones;
    }
}
