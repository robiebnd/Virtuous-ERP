package com.digipals.wms.common.document.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.entity.DocumentSequence;
import com.digipals.wms.common.document.repository.DocumentSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentNumberServiceImpl
        implements DocumentNumberService {

    private final DocumentSequenceRepository repository;

    @Override
    public synchronized String next(
            DocumentType documentType) {

        int financialYear = Year.now().getValue();

        DocumentSequence sequence =
                repository.findByDocumentTypeAndFinancialYear(
                                documentType.getPrefix(),
                                financialYear)
                        .orElseGet(() -> {

                            DocumentSequence newSequence =
                                    DocumentSequence.builder()

                                            .documentType(
                                                    documentType.getPrefix())

                                            .financialYear(
                                                    financialYear)

                                            .prefix(
                                                    documentType.getPrefix())

                                            .suffix("")

                                            .padding(6)

                                            .currentNumber(1L)

                                            .build();

                            return repository.save(newSequence);
                        });

        Long currentNumber =
                sequence.getCurrentNumber();

        sequence.setCurrentNumber(
                currentNumber + 1);

        repository.save(sequence);

        return format(
                sequence,
                currentNumber);
    }

    private String format(
            DocumentSequence sequence,
            Long number) {

        String suffix =
                sequence.getSuffix() == null
                        ? ""
                        : sequence.getSuffix();

        return String.format(
                "%s%0" + sequence.getPadding() + "d%s",
                sequence.getPrefix(),
                number,
                suffix);
    }
}