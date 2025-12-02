package controller;

import dao.GenericDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Disciplina;
import model.Professor;
import model.Turma;
import utils.AlertaUtil;

public class TurmaController {

    @FXML private TextField txtSemestre; // Ex: 2025/1
    @FXML private TextField txtHorario;  // Ex: Seg 08:00
    @FXML private ComboBox<Disciplina> cbDisciplina;
    @FXML private ComboBox<Professor> cbProfessor;

    @FXML private TableView<Turma> tabelaTurmas;
    @FXML private TableColumn<Turma, Long> colId;
    @FXML private TableColumn<Turma, String> colSemestre;
    @FXML private TableColumn<Turma, String> colDisciplina; // Mostra nome
    @FXML private TableColumn<Turma, String> colProfessor;  // Mostra nome
    @FXML private TableColumn<Turma, String> colHorario;

    private GenericDAO<Turma> turmaDAO = new GenericDAO<>(Turma.class);
    private GenericDAO<Disciplina> disciplinaDAO = new GenericDAO<>(Disciplina.class);
    private GenericDAO<Professor> professorDAO = new GenericDAO<>(Professor.class);

    private ObservableList<Turma> listaTurmas = FXCollections.observableArrayList();
    private Turma turmaSelecionada;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarCombos();
        atualizarTabela();
    }

    private void carregarCombos() {
        cbDisciplina.setItems(FXCollections.observableArrayList(disciplinaDAO.findAll()));
        cbProfessor.setItems(FXCollections.observableArrayList(professorDAO.findAll()));
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));


        colDisciplina.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDisciplina().getNome()));

        colProfessor.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProfessor().getNome()));
    }

    private void atualizarTabela() {
        listaTurmas.setAll(turmaDAO.findAll());
        tabelaTurmas.setItems(listaTurmas);
    }

    @FXML
    public void onSalvar() {
        try {
            String semestre = txtSemestre.getText();
            String horario = txtHorario.getText();
            Disciplina disciplina = cbDisciplina.getValue();
            Professor professor = cbProfessor.getValue();

            if (semestre.isEmpty() || disciplina == null || professor == null) {
                AlertaUtil.mostrarErro("Erro", "Semestre, Disciplina e Professor são obrigatórios!");
                return;
            }

            if (turmaSelecionada == null) {
                Turma t = new Turma();
                t.setSemestre(semestre);
                t.setHorario(horario);
                t.setDisciplina(disciplina);
                t.setProfessor(professor);
                turmaDAO.create(t);
                AlertaUtil.mostrarSucesso("Sucesso", "Turma criada!");
            } else {
                turmaSelecionada.setSemestre(semestre);
                turmaSelecionada.setHorario(horario);
                turmaSelecionada.setDisciplina(disciplina);
                turmaSelecionada.setProfessor(professor);
                turmaDAO.update(turmaSelecionada);
                AlertaUtil.mostrarSucesso("Sucesso", "Turma atualizada!");
                turmaSelecionada = null;
            }
            onLimpar();
            atualizarTabela();
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro", "Falha ao salvar turma.");
        }
    }

    @FXML
    public void onEditar() {
        Turma t = tabelaTurmas.getSelectionModel().getSelectedItem();
        if (t != null) {
            turmaSelecionada = t;
            txtSemestre.setText(t.getSemestre());
            txtHorario.setText(t.getHorario());
            cbDisciplina.setValue(t.getDisciplina());
            cbProfessor.setValue(t.getProfessor());
        } else {
            AlertaUtil.mostrarErro("Atenção", "Selecione uma turma.");
        }
    }

    @FXML
    public void onExcluir() {
        Turma t = tabelaTurmas.getSelectionModel().getSelectedItem();
        if (t != null) {
            turmaDAO.delete(t.getId());
            atualizarTabela();
        }
    }

    @FXML
    public void onLimpar() {
        txtSemestre.clear();
        txtHorario.clear();
        cbDisciplina.getSelectionModel().clearSelection();
        cbProfessor.getSelectionModel().clearSelection();
        turmaSelecionada = null;
    }
}