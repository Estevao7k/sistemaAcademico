package dao;
import model.Professor;

public class ProfessorDAO extends GenericDAO<Professor> {
    public ProfessorDAO() {
        super(Professor.class);
    }
}