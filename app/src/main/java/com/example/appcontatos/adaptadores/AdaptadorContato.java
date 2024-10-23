package com.example.appcontatos.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcontatos.R;
import com.example.appcontatos.modelos.Contato;

import java.util.List;

public class AdaptadorContato extends RecyclerView.Adapter<AdaptadorContato.ContactViewHolder> {

    // Lista de contatos
    private List<Contato> listaContatos;

    // Interface de callback para clique no contato
    private OnContactClickListener listener;

    // Construtor do adapter, recebendo a lista de contatos e o listener
    public AdaptadorContato(List<Contato> listaContatos, OnContactClickListener listener) {
        this.listaContatos = listaContatos;
        this.listener = listener;
    }

    // Interface para o clique no contato
    public interface OnContactClickListener {
        void onContactClick(Contato contato);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item de contato
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contato, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Pega o contato atual da lista
        Contato contato = listaContatos.get(position);

        // Define o nome do contato no TextView
        holder.contactNameTextView.setText(contato.getNome());

        // Concatena os números de telefone e seus tipos para exibir no TextView
        StringBuilder telefonesConcatenados = new StringBuilder();
        for (String[] telefone : contato.getTelefones()) {
            String tipoTelefone = telefone[0];
            String numeroTelefone = telefone[1];
            telefonesConcatenados.append(tipoTelefone).append(": ").append(numeroTelefone).append("\n");
        }
        // Remove o último "\n"
        if (telefonesConcatenados.length() > 0) {
            telefonesConcatenados.setLength(telefonesConcatenados.length() - 1);
        }

        // Define os números de telefone no TextView
        holder.contactPhoneTextView.setText(telefonesConcatenados.toString());

        // Define o listener de clique no item
        holder.itemView.setOnClickListener(v -> listener.onContactClick(contato));
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    // ViewHolder interno que contém as views do item de contato
    static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView contactNameTextView;
        TextView contactPhoneTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNameTextView = itemView.findViewById(R.id.contact_name);
            contactPhoneTextView = itemView.findViewById(R.id.contact_phone);
        }
    }
}
