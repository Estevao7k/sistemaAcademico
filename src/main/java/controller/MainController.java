package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import utils.AlertaUtil;

public class MainController {

    @FXML private BorderPane mainPane;

    @FXML
    public void abrirCursos() {
        carregarTela("/view/CursoView.fxml");
    }

    @FXML
    public void abrirDisciplinas() {
        carregarTela("/view/DisciplinaView.fxml");
    }

    @FXML
    public void abrirProfessores() {
        carregarTela("/view/ProfessorView.fxml");
    }

    @FXML
    public void abrirTurmas() {
        carregarTela("/view/TurmaView.fxml");
    }


    private void carregarTela(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxml));
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro de I/O", "Não foi possível carregar a tela: " + fxml + "\nErro: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro Crítico", "Arquivo FXML não encontrado: " + fxml);
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro Desconhecido", "Ocorreu um erro ao abrir a tela: " + e.getMessage());
        }
    }
}