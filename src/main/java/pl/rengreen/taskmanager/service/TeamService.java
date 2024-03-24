package pl.rengreen.taskmanager.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rengreen.taskmanager.model.Teams;
import pl.rengreen.taskmanager.repository.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Teams createTeam(Teams team) {
        return teamRepository.save(team);
    }

    @Transactional(readOnly = true)
    public Optional<Teams> getTeamById(Long teamId) {
        return teamRepository.findById(teamId);
    }

    @Transactional
    public void saveTeam(Teams team) {
        teamRepository.save(team);
    }
}
