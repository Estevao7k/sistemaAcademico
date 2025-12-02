package controller;

import dao.CursoDAO;
import dao.GenericDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Curso;
import model.Disciplina;
import utils.AlertaUtil;

public class DisciplinaController {

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Curso> cbCurso;

    @FXML private TableView<Disciplina> tabelaDisciplinas;
    @FXML private TableColumn<Disciplina, Long> colId;
    @FXML private TableColumn<Disciplina, String> colNome;
    @FXML private TableColumn<Disciplina, String> colDescricao;
    @FXML private TableColumn<Disciplina, String> colCurso;

    private GenericDAO<Disciplina> disciplinaDAO = new GenericDAO<>(Disciplina.class);
    private CursoDAO cursoDAO = new CursoDAO();

    private ObservableList<Disciplina> listaDisciplinas = FXCollections.observableArrayList();
    private Disciplina disciplinaSelecionada;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarCursos();
        atualizarTabela();
    }

    private void carregarCursos() {
        ObservableList<Curso> cursos = FXCollections.observableArrayList(cursoDAO.findAll());
        cbCurso.setItems(cursos);
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        colCurso.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCurso() != null) {
                return new SimpleStringProperty(cellData.getValue().getCurso().getNome());
            }
            return new SimpleStringProperty("-");
        });
    }

    private void atualizarTabela() {
        try {
            listaDisciplinas.setAll(disciplinaDAO.findAll());
            tabelaDisciplinas.setItems(listaDisciplinas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSalvar() {
        try {
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();
            Curso cursoSelecionadoCombo = cbCurso.getValue();

            if (nome.isEmpty() || cursoSelecionadoCombo == null) {
                AlertaUtil.mostrarErro("Erro", "Nome e Curso são obrigatórios!");
                return;
            }

            if (disciplinaSelecionada == null) {
                Disciplina nova = new Disciplina();
                nova.setNome(nome);
                nova.setDescricao(descricao);
                nova.setCurso(cursoSelecionadoCombo);
                disciplinaDAO.create(nova);
                AlertaUtil.mostrarSucesso("Sucesso", "Disciplina salva!");
            } else {
                disciplinaSelecionada.setNome(nome);
                disciplinaSelecionada.setDescricao(descricao);
                disciplinaSelecionada.setCurso(cursoSelecionadoCombo);
                disciplinaDAO.update(disciplinaSelecionada);
                AlertaUtil.mostrarSucesso("Sucesso", "Disciplina atualizada!");
                disciplinaSelecionada = null;
            }

            onLimpar();
            atualizarTabela();
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro", "Falha ao salvar: " + e.getMessage());
        }
    }

    @FXML
    public void onEditar() {
        Disciplina d = tabelaDisciplinas.getSelectionModel().getSelectedItem();
        if (d != null) {
            disciplinaSelecionada = d;
            txtNome.setText(d.getNome());
            txtDescricao.setText(d.getDescricao());
            cbCurso.setValue(d.getCurso());
        } else {
            AlertaUtil.mostrarErro("Atenção", "Selecione para editar.");
        }
    }

    @FXML
    public void onExcluir() {
        Disciplina d = tabelaDisciplinas.getSelectionModel().getSelectedItem();
        if (d != null) {
            disciplinaDAO.delete(d.getId());
            atualizarTabela();
        }
    }

    @FXML
    public void onLimpar() {
        txtNome.clear();
        txtDescricao.clear();
        cbCurso.getSelectionModel().clearSelection();
        disciplinaSelecionada = null;
    }
}