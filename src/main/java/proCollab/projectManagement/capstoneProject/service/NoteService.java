package proCollab.projectManagement.capstoneProject.service;

import java.util.List;
import java.util.Optional;

import proCollab.projectManagement.capstoneProject.model.Note;
import proCollab.projectManagement.capstoneProject.model.User;

public interface NoteService {

    List<Note> getAllNotesByUser(User user);

    Optional<Note> getNoteById(Long id);

    Note createNote(Note note);

    Note updateNote(Note note);

    void deleteNoteById(Long id);
}
