package com.digipals.wms.putaway.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PutAwayMapper;
import com.digipals.wms.putaway.dto.AssignPutAwayRequest;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.entity.PutAway;
import com.digipals.wms.putaway.entity.PutAwayStatus;
import com.digipals.wms.putaway.repository.PutAwayLineRepository;
import com.digipals.wms.putaway.repository.PutAwayRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PutAwayAssignmentServiceImpl implements PutAwayAssignmentService {

    private final PutAwayRepository putAwayRepository;
    private final PutAwayLineRepository putAwayLineRepository;
    private final UserRepository userRepository;

    @Override
    public PutAwayResponse assign(
            UUID putAwayId,
            AssignPutAwayRequest request) {

        PutAway putAway = putAwayRepository.findById(putAwayId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Put-Away not found."));

        if (putAway.getStatus() != PutAwayStatus.DRAFT) {
            throw new InvalidWorkflowException(
                    "Only draft Put-Aways can be assigned.");
        }

        User assignee = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assigned user not found."));

        if (!Boolean.TRUE.equals(assignee.getActive())) {
            throw new InvalidWorkflowException(
                    "Assigned user is not active.");
        }

        putAway.setAssignedTo(assignee);
        putAway = putAwayRepository.save(putAway);

        return PutAwayMapper.toResponse(
                putAway,
                putAwayLineRepository.findByPutAwayId(putAway.getId()));
    }
}
