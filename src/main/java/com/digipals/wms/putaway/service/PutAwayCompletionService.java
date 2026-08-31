package com.digipals.wms.putaway.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PutAwayMapper;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.entity.PutAway;
import com.digipals.wms.putaway.entity.PutAwayLine;
import com.digipals.wms.putaway.entity.PutAwayLineStatus;
import com.digipals.wms.putaway.entity.PutAwayStatus;
import com.digipals.wms.putaway.repository.PutAwayLineRepository;
import com.digipals.wms.putaway.repository.PutAwayRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PutAwayCompletionService {

    private final PutAwayRepository putAwayRepository;
    private final PutAwayLineRepository putAwayLineRepository;
    private final CurrentUserService currentUserService;

    public PutAwayResponse complete(UUID putAwayId) {
        PutAway putAway = putAwayRepository.findById(putAwayId)
                .orElseThrow(() -> new ResourceNotFoundException("Put-Away not found."));

        if (putAway.getStatus() == PutAwayStatus.COMPLETED) {
            return toResponse(putAway);
        }

        if (putAway.getStatus() == PutAwayStatus.CANCELLED) {
            throw new InvalidWorkflowException("Cancelled Put-Aways cannot be completed.");
        }

        List<PutAwayLine> lines = putAwayLineRepository.findByPutAwayId(putAway.getId());
        if (lines.isEmpty()) {
            throw new InvalidWorkflowException("Put-Away cannot be completed without lines.");
        }

        boolean allCompleted = lines.stream()
                .allMatch(line -> line.getStatus() == PutAwayLineStatus.COMPLETED);

        if (!allCompleted) {
            throw new InvalidWorkflowException(
                    "Put-Away cannot be completed until all lines are fully put away.");
        }

        User currentUser = currentUserService.getCurrentUser();
        putAway.setStatus(PutAwayStatus.COMPLETED);
        putAway.setCompletedBy(currentUser);
        putAway.setCompletedAt(LocalDateTime.now());

        return toResponse(putAwayRepository.save(putAway));
    }

    private PutAwayResponse toResponse(PutAway putAway) {
        return PutAwayMapper.toResponse(
                putAway,
                putAwayLineRepository.findByPutAwayId(putAway.getId()));
    }
}
