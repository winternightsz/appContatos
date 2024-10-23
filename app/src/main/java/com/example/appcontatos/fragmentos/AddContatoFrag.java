package com.example.appcontatos.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.appcontatos.R;
import com.example.appcontatos.database.BancoHelper;

import java.util.ArrayList;
import java.util.List;

public class AddContatoFrag extends Fragment {

    private EditText nameInput;
    private LinearLayout phoneContainer;
    private Button saveButton, addPhoneButton;

    private final List<View> phoneEntries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout para este fragmento
        View view = inflater.inflate(R.layout.fragmento_add_contato, container, false);

        nameInput = view.findViewById(R.id.name_input);
        phoneContainer = view.findViewById(R.id.phone_container);
        saveButton = view.findViewById(R.id.save_button);
        addPhoneButton = view.findViewById(R.id.add_phone_button);

        // Configura o botão para adicionar novos números de telefone
        addPhoneButton.setOnClickListener(v -> addPhoneField());

        // Define o clique do botão de salvar
        saveButton.setOnClickListener(v -> saveContact());

        return view;
    }

    // Método para adicionar um novo campo de telefone dinamicamente
    private void addPhoneField() {
        View phoneEntry = getLayoutInflater().inflate(R.layout.telefone_dinamico, phoneContainer, false);

        Spinner phoneTypeSpinner = phoneEntry.findViewById(R.id.phone_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.phone_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneTypeSpinner.setAdapter(adapter);

        phoneContainer.addView(phoneEntry);
        phoneEntries.add(phoneEntry);
    }

    // Método para salvar o contato
    private void saveContact() {
        String nome = nameInput.getText().toString().trim();

        // Valida o nome do contato
        if (nome.isEmpty()) {
            Toast.makeText(getActivity(), "Preencha o nome", Toast.LENGTH_SHORT).show();
            return;
        }

        // Valida se pelo menos um telefone foi adicionado
        if (phoneEntries.isEmpty()) {
            Toast.makeText(getActivity(), "Adicione ao menos um telefone", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insere o nome do contato no banco de dados
        BancoHelper dbHelper = new BancoHelper(getActivity());
        long contatoId = dbHelper.adicionarContato(nome);

        if (contatoId == -1) {
            Toast.makeText(getActivity(), "Erro ao adicionar o contato", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> telefonesInseridos = new ArrayList<>();

        // Valida e insere os números de telefone associados ao contato
        for (View entry : phoneEntries) {
            EditText phoneInput = entry.findViewById(R.id.phone_input);
            Spinner phoneTypeSpinner = entry.findViewById(R.id.phone_type_spinner);
            String numeroTelefone = phoneInput.getText().toString().trim();
            String tipoTelefone = phoneTypeSpinner.getSelectedItem().toString();

            // Valida se o número de telefone foi preenchido
            if (numeroTelefone.isEmpty()) {
                Toast.makeText(getActivity(), "Preencha todos os números de telefone", Toast.LENGTH_SHORT).show();
                return;
            }

            // Valida se o tipo de telefone foi selecionado (evitar tipo padrão como "Selecione")
            if (tipoTelefone.equals("Selecione um tipo")) {
                Toast.makeText(getActivity(), "Selecione um tipo de telefone válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Valida se o número já foi inserido (duplicidade)
            if (telefonesInseridos.contains(numeroTelefone)) {
                Toast.makeText(getActivity(), "Número de telefone duplicado: " + numeroTelefone, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação de formato de telefone (exemplo simples de regex para números)
            if (!numeroTelefone.matches("\\d{10,11}")) { // Aceita números com 10 ou 11 dígitos
                Toast.makeText(getActivity(), "Número de telefone inválido: " + numeroTelefone, Toast.LENGTH_SHORT).show();
                return;
            }

            // Se tudo estiver válido, insere o telefone
            boolean isPhoneInserted = dbHelper.adicionarTelefone(contatoId, tipoTelefone, numeroTelefone);
            if (!isPhoneInserted) {
                Toast.makeText(getActivity(), "Erro ao adicionar telefone", Toast.LENGTH_SHORT).show();
                return;
            }

            telefonesInseridos.add(numeroTelefone); // Adiciona o número à lista para verificar duplicidade
        }

        // Se tudo foi salvo corretamente
        Toast.makeText(getActivity(), "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
        if (isAdded()) {
            getParentFragmentManager().popBackStack();
        }
    }

}
