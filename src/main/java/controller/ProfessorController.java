package controller;

import dao.GenericDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Professor;
import utils.AlertaUtil;

public class ProfessorController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtFormacao;

    @FXML private TableView<Professor> tabelaProfessores;
    @FXML private TableColumn<Professor, Long> colId;
    @FXML private TableColumn<Professor, String> colNome;
    @FXML private TableColumn<Professor, String> colEmail;
    @FXML private TableColumn<Professor, String> colFormacao;

    private GenericDAO<Professor> dao = new GenericDAO<>(Professor.class);
    private ObservableList<Professor> lista = FXCollections.observableArrayList();
    private Professor selecionado;

    @FXML
    public void initialize() {
        configurarColunas();
        atualizarTabela();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFormacao.setCellValueFactory(new PropertyValueFactory<>("formacao"));
    }

    private void atualizarTabela() {
        try {
            lista.setAll(dao.findAll());
            tabelaProfessores.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSalvar() {
        try {
            String nome = txtNome.getText();
            String email = txtEmail.getText();
            String formacao = txtFormacao.getText();

            if (nome.isEmpty()) {
                AlertaUtil.mostrarErro("Erro", "Nome é obrigatório.");
                return;
            }

            if (selecionado == null) {
                Professor p = new Professor();
                p.setNome(nome);
                p.setEmail(email);
                p.setFormacao(formacao);
                dao.create(p);
                AlertaUtil.mostrarSucesso("Sucesso", "Professor cadastrado!");
            } else {
                selecionado.setNome(nome);
                selecionado.setEmail(email);
                selecionado.setFormacao(formacao);
                dao.update(selecionado);
                AlertaUtil.mostrarSucesso("Sucesso", "Professor atualizado!");
                selecionado = null;
            }
            onLimpar();
            atualizarTabela();
        } catch (Exception e) {
            AlertaUtil.mostrarErro("Erro", "Falha ao salvar: " + e.getMessage());
        }
    }

    @FXML
    public void onEditar() {
        Professor p = tabelaProfessores.getSelectionModel().getSelectedItem();
        if (p != null) {
            selecionado = p;
            txtNome.setText(p.getNome());
            txtEmail.setText(p.getEmail());
            txtFormacao.setText(p.getFormacao());
        } else {
            AlertaUtil.mostrarErro("Atenção", "Selecione um professor.");
        }
    }

    @FXML
    public void onExcluir() {
        Professor p = tabelaProfessores.getSelectionModel().getSelectedItem();
        if (p != null) {
            dao.delete(p.getId());
            atualizarTabela();
        }
    }

    @FXML
    public void onLimpar() {
        txtNome.clear();
        txtEmail.clear();
        txtFormacao.clear();
        selecionado = null;
    }
}