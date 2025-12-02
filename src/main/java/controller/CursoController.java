package controller;

import dao.CursoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Curso;
import utils.AlertaUtil;

public class CursoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCarga;
    @FXML private TableView<Curso> tabelaCursos;
    @FXML private TableColumn<Curso, Long> colId;
    @FXML private TableColumn<Curso, String> colNome;
    @FXML private TableColumn<Curso, Integer> colCarga;

    private CursoDAO cursoDAO = new CursoDAO();

    private ObservableList<Curso> listaCursos = FXCollections.observableArrayList();
    private Curso cursoSelecionado;

    @FXML
    public void initialize() {
        System.out.println(">>> [DEBUG] O Controller foi inicializado!");
        configurarColunas();
        atualizarTabela();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCarga.setCellValueFactory(new PropertyValueFactory<>("cargaHoraria"));
    }

    private void atualizarTabela() {
        try {
            System.out.println(">>> [DEBUG] Tentando buscar cursos no banco...");
            listaCursos.setAll(cursoDAO.findAll());
            tabelaCursos.setItems(listaCursos);
            System.out.println(">>> [DEBUG] Tabela atualizada com " + listaCursos.size() + " cursos.");
        } catch (Exception e) {
            System.out.println(">>> [ERRO CRÍTICO] Falha ao listar cursos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onSalvar() {
        System.out.println(">>> [DEBUG] Botão SALVAR clicado!");

        try {
            String nome = txtNome.getText();
            String cargaStr = txtCarga.getText();

            System.out.println(">>> [DEBUG] Dados capturados: Nome=" + nome + ", Carga=" + cargaStr);

            if (nome.isEmpty() || cargaStr.isEmpty()) {
                AlertaUtil.mostrarErro("Erro", "Preencha todos os campos!");
                return;
            }

            Integer carga = Integer.parseInt(cargaStr);

            if (cursoSelecionado == null) {
                System.out.println(">>> [DEBUG] Criando novo curso...");
                Curso novo = new Curso();
                novo.setNome(nome);
                novo.setCargaHoraria(carga);
                cursoDAO.create(novo);
                AlertaUtil.mostrarSucesso("Sucesso", "Curso salvo!");
            } else {
                System.out.println(">>> [DEBUG] Atualizando curso existente...");
                cursoSelecionado.setNome(nome);
                cursoSelecionado.setCargaHoraria(carga);
                cursoDAO.update(cursoSelecionado);
                AlertaUtil.mostrarSucesso("Sucesso", "Curso atualizado!");
                cursoSelecionado = null;
            }

            onLimpar();
            atualizarTabela();

        } catch (NumberFormatException e) {
            System.out.println(">>> [ERRO] Carga horária inválida");
            AlertaUtil.mostrarErro("Erro", "A carga horária deve ser um número inteiro.");
        } catch (Exception e) {
            System.out.println(">>> [ERRO CRÍTICO] Erro ao salvar no banco:");
            e.printStackTrace(); // Mostra o motivo exato se for erro de SQL/Conexão
            AlertaUtil.mostrarErro("Erro de Sistema", "Falha ao salvar: " + e.getMessage());
        }
    }

    @FXML
    public void onEditar() {
        System.out.println(">>> [DEBUG] Botão EDITAR clicado");
        Curso curso = tabelaCursos.getSelectionModel().getSelectedItem();
        if (curso != null) {
            cursoSelecionado = curso;
            txtNome.setText(curso.getNome());
            txtCarga.setText(String.valueOf(curso.getCargaHoraria()));
        } else {
            AlertaUtil.mostrarErro("Atenção", "Selecione um curso na tabela.");
        }
    }

    @FXML
    public void onExcluir() {
        System.out.println(">>> [DEBUG] Botão EXCLUIR clicado");
        Curso curso = tabelaCursos.getSelectionModel().getSelectedItem();
        if (curso != null) {
            try {
                cursoDAO.delete(curso.getId());
                atualizarTabela();
                AlertaUtil.mostrarSucesso("Sucesso", "Curso removido.");
            } catch (Exception e) {
                e.printStackTrace();
                AlertaUtil.mostrarErro("Erro", "Erro ao excluir (verifique o console).");
            }
        } else {
            AlertaUtil.mostrarErro("Atenção", "Selecione para excluir.");
        }
    }

    @FXML
    public void onLimpar() {
        txtNome.clear();
        txtCarga.clear();
        cursoSelecionado = null;
    }
}