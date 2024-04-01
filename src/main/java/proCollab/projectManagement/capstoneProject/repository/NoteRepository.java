package proCollab.projectManagement.capstoneProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proCollab.projectManagement.capstoneProject.model.Note;
import proCollab.projectManagement.capstoneProject.model.User;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("SELECT n FROM Note n WHERE n.note_owner = :user")
    List<Note> findByNoteOwner(@Param("user") User user);
}
