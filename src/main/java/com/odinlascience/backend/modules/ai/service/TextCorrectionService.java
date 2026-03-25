package com.odinlascience.backend.modules.ai.service;

import com.odinlascience.backend.modules.ai.dto.CorrectionResponse;

public interface TextCorrectionService {

    CorrectionResponse correct(String text, String language);
}
