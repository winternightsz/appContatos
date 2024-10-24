package com.example.appcontatos.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.appcontatos.R;
import com.example.appcontatos.database.BancoHelper;
import com.example.appcontatos.modelos.Contato;

import java.util.ArrayList;
import java.util.List;

public class EditContatoFrag extends Fragment {

    private EditText nameInput;
    private EditText phoneInput;
    private Spinner phoneTypeSpinner;
    private Button updateButton, deleteButton;
    private int contatoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_edit_contato, container, false);

        nameInput = view.findViewById(R.id.name_input);
        phoneInput = view.findViewById(R.id.phone_input);
        phoneTypeSpinner = view.findViewById(R.id.phone_type_spinner);
        updateButton = view.findViewById(R.id.update_button);
        deleteButton = view.findViewById(R.id.delete_button);

        // Obtém os dados do contato passado como argumento
        assert getArguments() != null;
        contatoId = getArguments().getInt("contactId");

        BancoHelper dbHelper = new BancoHelper(getActivity());
        Contato contato = dbHelper.obterContato(contatoId);

        if (contato != null) {
            // Preenche o nome do contato no layout
            nameInput.setText(contato.getNome());

            // Configura o Spinner com os tipos de telefone
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.phone_types,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            phoneTypeSpinner.setAdapter(adapter);

            // Verifica se há telefones associados ao contato
            if (!contato.getTelefones().isEmpty()) {
                // Se houver telefones, preenche o primeiro número de telefone e o tipo
                String[] primeiroTelefone = contato.getTelefones().get(0);
                phoneInput.setText(primeiroTelefone[1]);  // Posição 1 é o número de telefone
                int spinnerPosition = adapter.getPosition(primeiroTelefone[0]); // Posição 0 é o tipo de telefone
                phoneTypeSpinner.setSelection(spinnerPosition);
            } else {
                // Se não houver telefones, deixa o campo de telefone vazio e define o spinner no valor padrão
                phoneInput.setText("");
                phoneTypeSpinner.setSelection(0);  // Assumindo que o valor 0 seja "Selecione um tipo"
            }
        }

        updateButton.setOnClickListener(v -> updateContact());
        deleteButton.setOnClickListener(v -> deleteContact());

        return view;
    }

    // Método para atualizar o contato
    private void updateContact() {
        String nome = nameInput.getText().toString().trim();
        String numeroTelefone = phoneInput.getText().toString().trim();
        String tipoTelefone = phoneTypeSpinner.getSelectedItem().toString();

        // Validação do nome
        if (nome.isEmpty()) {
            Toast.makeText(getActivity(), "Preencha o nome", Toast.LENGTH_SHORT).show();
            return;
        }

        BancoHelper dbHelper = new BancoHelper(getActivity());

        List<String[]> telefones = new ArrayList<>();

        // Se o usuário preencher o número de telefone, adiciona o número e o tipo à lista de telefones
        if (!numeroTelefone.isEmpty()) {
            // Validação do tipo de telefone
            if (tipoTelefone.equals("Selecione um tipo")) {
                Toast.makeText(getActivity(), "Selecione um tipo de telefone válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação de formato de telefone (exemplo simples de regex para números)
            if (!numeroTelefone.matches("\\d{10,11}")) { // Aceita números com 10 ou 11 dígitos
                Toast.makeText(getActivity(), "Número de telefone inválido: " + numeroTelefone, Toast.LENGTH_SHORT).show();
                return;
            }

            // Adiciona o telefone à lista
            telefones.add(new String[]{tipoTelefone, numeroTelefone});
        }

        // Atualiza o contato com o nome e o(s) número(s) de telefone
        boolean isUpdated = dbHelper.atualizarContato(contatoId, nome, telefones);

        if (isUpdated) {
            Toast.makeText(getActivity(), "Contato atualizado com sucesso", Toast.LENGTH_SHORT).show();
            if (isAdded()) {
                getParentFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(getActivity(), "Erro ao atualizar contato", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para excluir o contato
    private void deleteContact() {
        BancoHelper dbHelper = new BancoHelper(getActivity());
        boolean isDeleted = dbHelper.deletarContato(contatoId);

        if (isDeleted) {
            Toast.makeText(getActivity(), "Contato excluído com sucesso", Toast.LENGTH_SHORT).show();
            if (isAdded()) {
                getParentFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(getActivity(), "Erro ao excluir contato", Toast.LENGTH_SHORT).show();
        }
    }
}
