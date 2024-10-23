package com.example.appcontatos.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcontatos.MainActivity;
import com.example.appcontatos.R;
import com.example.appcontatos.adaptadores.AdaptadorContato;
import com.example.appcontatos.database.BancoHelper;
import com.example.appcontatos.modelos.Contato;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListaContatoFrag extends Fragment {

    private RecyclerView recyclerView;
    private AdaptadorContato adapter;
    private BancoHelper dbHelper;
    private FloatingActionButton fabAddContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_lista_contato, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new BancoHelper(getActivity());

        fabAddContact = view.findViewById(R.id.fab_add_contact);

        // Configura o botão flutuante para adicionar um novo contato
        fabAddContact.setOnClickListener(v -> {
            AddContatoFrag addContatoFragment = new AddContatoFrag();
            ((MainActivity) requireActivity()).switchToFragment(addContatoFragment);
        });

        // Carrega os contatos e configura o adaptador
        carregarContatos();

        return view;
    }

    private void carregarContatos() {
        List<Contato> listaContatos = dbHelper.obterTodosContatos();
        adapter = new AdaptadorContato(listaContatos, contato -> {
            // Abrir EditContatoFrag ao clicar em um contato
            EditContatoFrag editContatoFragment = new EditContatoFrag();
            Bundle args = new Bundle();
            args.putInt("contactId", contato.getId());
            editContatoFragment.setArguments(args);

            ((MainActivity) requireActivity()).switchToFragment(editContatoFragment);
        });
        recyclerView.setAdapter(adapter);
    }
}
