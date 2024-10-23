package com.example.appcontatos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.appcontatos.modelos.Contato;

import java.util.ArrayList;
import java.util.List;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contatos.db";
    private static final int DATABASE_VERSION = 2; // Atualizado para suportar a nova versão do banco de dados

    public BancoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Cria a tabela de contatos
        String criarTabelaContatos = "CREATE TABLE Contatos (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Nome TEXT NOT NULL)";
        db.execSQL(criarTabelaContatos);

        // Cria a tabela de telefones
        String criarTabelaTelefones = "CREATE TABLE Telefones (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ContatoID INTEGER NOT NULL, " +
                "TipoTelefone TEXT, " +
                "NumeroTelefone TEXT, " +
                "FOREIGN KEY (ContatoID) REFERENCES Contatos(ID) ON DELETE CASCADE)";
        db.execSQL(criarTabelaTelefones);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Telefones");
        db.execSQL("DROP TABLE IF EXISTS Contatos");
        onCreate(db);
    }

    // Método para adicionar um contato (apenas o nome)
    public long adicionarContato(String nome) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Nome", nome);

        // Insere o contato e retorna o ID do contato
        return db.insert("Contatos", null, values);
    }

    // Método para adicionar um número de telefone associado a um contato
    public boolean adicionarTelefone(long contatoId, String tipoTelefone, String numeroTelefone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ContatoID", contatoId);
        values.put("TipoTelefone", tipoTelefone);
        values.put("NumeroTelefone", numeroTelefone);

        // Insere o telefone na tabela Telefones
        long resultado = db.insert("Telefones", null, values);
        return resultado != -1;
    }

    // Método para obter um contato com seus números de telefone
    public Contato obterContato(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Obter os dados básicos do contato
        Cursor cursorContato = db.query("Contatos", null, "ID=?", new String[]{String.valueOf(id)}, null, null, null);
        Contato contato = null;
        if (cursorContato != null && cursorContato.moveToFirst()) {
            contato = new Contato(cursorContato.getInt(0), cursorContato.getString(1), new ArrayList<>());
            cursorContato.close();

            // Obter os números de telefone associados ao contato
            Cursor cursorTelefone = db.query("Telefones", null, "ContatoID=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursorTelefone.moveToFirst()) {
                // Verifica se as colunas "TipoTelefone" e "NumeroTelefone" existem no cursor
                int tipoTelefoneIndex = cursorTelefone.getColumnIndex("TipoTelefone");
                int numeroTelefoneIndex = cursorTelefone.getColumnIndex("NumeroTelefone");

                if (tipoTelefoneIndex != -1 && numeroTelefoneIndex != -1) {
                    do {
                        String tipoTelefone = cursorTelefone.getString(tipoTelefoneIndex);
                        String numeroTelefone = cursorTelefone.getString(numeroTelefoneIndex);
                        contato.addTelefone(tipoTelefone, numeroTelefone);  // Método na classe Contato para adicionar o número à lista
                    } while (cursorTelefone.moveToNext());
                } else {
                    // Caso as colunas não existam, lançar uma exceção ou fazer log
                    throw new IllegalStateException("Colunas TipoTelefone ou NumeroTelefone não encontradas.");
                }
            }
            cursorTelefone.close();
        }
        return contato;
    }

    public List<Contato> obterTodosContatos() {
        List<Contato> listaContatos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Obter todos os contatos
        Cursor cursorContato = db.rawQuery("SELECT * FROM Contatos ORDER BY Nome ASC", null);
        if (cursorContato.moveToFirst()) {
            do {
                int contatoId = cursorContato.getInt(0);
                String nomeContato = cursorContato.getString(1);

                Contato contato = new Contato(contatoId, nomeContato, new ArrayList<>());

                // Obter os números de telefone para o contato atual
                Cursor cursorTelefone = db.query("Telefones", null, "ContatoID=?", new String[]{String.valueOf(contatoId)}, null, null, null);
                if (cursorTelefone.moveToFirst()) {
                    // Verifica se as colunas "TipoTelefone" e "NumeroTelefone" existem no cursor
                    int tipoTelefoneIndex = cursorTelefone.getColumnIndex("TipoTelefone");
                    int numeroTelefoneIndex = cursorTelefone.getColumnIndex("NumeroTelefone");

                    if (tipoTelefoneIndex != -1 && numeroTelefoneIndex != -1) {
                        do {
                            String tipoTelefone = cursorTelefone.getString(tipoTelefoneIndex);
                            String numeroTelefone = cursorTelefone.getString(numeroTelefoneIndex);
                            contato.addTelefone(tipoTelefone, numeroTelefone);  // Método na classe Contato para adicionar o número à lista
                        } while (cursorTelefone.moveToNext());
                    } else {
                        // Caso as colunas não existam, lançar uma exceção ou fazer log
                        throw new IllegalStateException("Colunas TipoTelefone ou NumeroTelefone não encontradas.");
                    }
                }
                cursorTelefone.close();

                listaContatos.add(contato);
            } while (cursorContato.moveToNext());
        }
        cursorContato.close();
        return listaContatos;
    }


    // Método para atualizar um contato existente
    public boolean atualizarContato(int id, String nome, List<String[]> telefones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Nome", nome);

        // Atualiza o nome do contato
        int resultado = db.update("Contatos", values, "ID=?", new String[]{String.valueOf(id)});
        if (resultado <= 0) {
            return false;
        }

        // Atualiza os números de telefone (removendo os antigos e adicionando os novos)
        db.delete("Telefones", "ContatoID=?", new String[]{String.valueOf(id)});
        for (String[] telefone : telefones) {
            adicionarTelefone(id, telefone[0], telefone[1]); // telefone[0] = tipoTelefone, telefone[1] = numeroTelefone
        }
        return true;
    }

    // Método para deletar um contato
    public boolean deletarContato(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("Contatos", "ID=?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }

}

